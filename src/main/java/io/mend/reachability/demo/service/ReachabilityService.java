package io.mend.reachability.demo.service;

import io.mend.reachability.demo.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReachabilityService {

    private final ScaClientService scaClient;
    private final FileUtilsService fileUtils;
    private final ProcessRunnerService processRunner;

    public ReachabilityService(ScaClientService scaClient, FileUtilsService fileUtils, ProcessRunnerService processRunner) {
        this.scaClient = scaClient;
        this.fileUtils = fileUtils;
        this.processRunner = processRunner;
    }

    public StatsDto fetchReachabilityData(ArgsDto argsDto) {
        Date startTime = new Date();
        log.info(">> fetchReachabilityData: {}", argsDto.toString());

        StatsDto stats = new StatsDto();

        LocalDate start = argsDto.getFromDate();
        LocalDate end = argsDto.getToDate();

        for (LocalDate date = start; !date.isAfter(end) && !date.isEqual(end); date = date.plusDays(1)) {
            stats.incrementNumOfDays();

            List<VulnerabilityDto> vulnerabilities = scaClient.getAllVulnerabilitiesBetweenDates(date, date.plusDays(1), argsDto.getPartnerToken() );
            stats.addToNumOfVulnerabilities(vulnerabilities.size());

            //Unify the resource vulnerabilities and filter the empty once
            Set<ResourceVulnerabilityDto> resourceVulnerabilities = vulnerabilities
                    .stream()
                    .flatMap(x -> x.getResourceVulnerabilities().stream())
                    .filter(x -> !x.isEmpty())
                    .collect(Collectors.toSet());
            stats.addToNumOfResourceVulnerabilities(resourceVulnerabilities.size());

            fetchVulnerabilityElements(resourceVulnerabilities, argsDto, stats);
        }


        log.info("<< fetchReachabilityData completed in: {} mSec", new Date().getTime() - startTime.getTime());
        return stats;
    }

    private void fetchVulnerabilityElements(Set<ResourceVulnerabilityDto> resourceVulnerabilities, ArgsDto argsDto, StatsDto stats) {

        //Send all resourceVulnerability to fetch their elements asynchronously
        List<CompletableFuture<Pair<ResourceVulnerabilityDto, VulElementResponseDto>>> futures = new ArrayList<>();
        for (ResourceVulnerabilityDto resourceVulnerability : resourceVulnerabilities) {
            futures.add(scaClient.getAllVulnerableElements(resourceVulnerability, argsDto.getPartnerToken()));
        }

        waitForResults(futures, argsDto, stats);
    }

    private void waitForResults(List<CompletableFuture<Pair<ResourceVulnerabilityDto, VulElementResponseDto>>> futures, ArgsDto argsDto, StatsDto stats) {

        int counter = 0;
        for (CompletableFuture<Pair<ResourceVulnerabilityDto, VulElementResponseDto>> future : futures) {
            counter++;
            if (counter == 1 || counter % 1000 == 0 || counter == futures.size()) {
                log.info("Getting information for: {} out of {}", counter, futures.size());
            }

            try {
                Pair<ResourceVulnerabilityDto, VulElementResponseDto> pairResult = future.get();
                ResourceVulnerabilityDto resourceVulnerability = pairResult.getLeft();
                VulElementResponseDto vulnerableElements = pairResult.getRight();

                //Save the request and response to disk
                if (vulnerableElements != null && !vulnerableElements.getSha1ToVulnerableElements().isEmpty()) {
                    if (vulnerableElements.getSha1ToVulnerableElements().size() == 1) {
                        for (Map.Entry<String, List<Sha1ToVulnerableElementDto>> entry : vulnerableElements.getSha1ToVulnerableElements().entrySet()) {
                            long numOfElements = entry.getValue().stream().mapToLong(x -> x.getVulnerableEntities().size()).sum();
                            if (!entry.getValue().isEmpty() &&  numOfElements> 0) {

                                String language = getLanguage(entry);
                                if (language != null) {
                                    FileStructure fs = new FileStructure(argsDto.getOutputPath(), resourceVulnerability.calcRelativePath(language), argsDto.getSrcRootPath());

                                    fileUtils.saveFileAsJson(resourceVulnerability, fs.getRequestPath().toString());
                                    fileUtils.saveFileAsJson(vulnerableElements, fs.getResponsePath().toString());
                                    stats.incrementNumOfVulnerabilitiesWithRelevantElements();
                                    stats.addToNumOfElements( (int)numOfElements );

                                    if (argsDto.isScan()) {
                                        boolean executionResult;
                                        if (fs.getSrcPath().toFile().exists()) {

                                            executionResult = executeCli(argsDto.getMendExecPath(), fs);
                                            stats.incrementNumOfEngineExecutions();
                                            if (!executionResult) {
                                                stats.incrementNumOfEngineFailures();
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        log.warn("Got more than one result --> This should never happen");
                    }
                }


            } catch (InterruptedException | ExecutionException e) {
                log.warn("Failed to get elements with error: {}. Ignoring this library", e.getMessage());
            }
        }


    }

    private boolean executeCli(Path cliExecPath, FileStructure fs) {

        List<String> procArgs = new ArrayList<>();
        procArgs.add(cliExecPath.toString());
        procArgs.add("code");
        procArgs.add("--internal__reachability-mode");
        procArgs.add("--dir");
        procArgs.add(fs.getSrcPath().toString());

        procArgs.add("--internal__reachability-mode-results-directory");
        procArgs.add(fs.getVulResultPath().toString());
        fs.getVulResultPath().toFile().mkdirs();

        procArgs.add("--internal__reachability-mode-input-file");
        procArgs.add(fs.getResponsePath().toString());

        procArgs.add("--saveid");
        procArgs.add(fs.getVulResultSaveIdPath().toString());

        ProcessRunnerService.ProcessRunnerConfig.ProcessRunnerConfigBuilder configBuilder = ProcessRunnerService.ProcessRunnerConfig.builder();
        configBuilder.processArgs(procArgs);
        configBuilder.workingDirectory(fs.getWorkingPath());

        try {
            processRunner.runSynchronously(configBuilder.build());
        } catch (InterruptedException | IOException e) {
            log.error("Failed to execute CLI on folder: {}", fs.getSrcPath(), e);
            return false;
        }

        return true;
    }


    private String getLanguage(Map.Entry<String, List<Sha1ToVulnerableElementDto>> entry) {
        //return the first langauge
        return entry
                .getValue()
                .stream()
                .filter(x-> x.getVulnerableEntities() != null)
                .filter(x-> !x.getVulnerableEntities().isEmpty())
                .flatMap(x-> x.getVulnerableEntities().stream())
                .map(VulnerableEntity::getLanguage)
                .filter(Objects::nonNull)
                .filter(this::isLanguageSupported)
                .findFirst()
                .orElse(null);
    }

    private boolean isLanguageSupported(String language){
        return language.equals("c_sharp") || language.equals("java") || language.equals("java_script") || language.equals("python");
    }

}
