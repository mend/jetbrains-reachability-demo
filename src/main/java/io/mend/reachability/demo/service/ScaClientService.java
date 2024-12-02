package io.mend.reachability.demo.service;

import io.mend.reachability.demo.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ScaClientService {

    private final static String URI = "https://staging.whitesourcesoftware.com/partner";

    private final WebClient webClient;

    public ScaClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<VulnerabilityDto> getAllVulnerabilitiesBetweenDates(LocalDate from, LocalDate to, String partnerToken, List<String> findSha1s){

        log.info(">> getAllVulnerabilitiesBetweenDates: from: {}, to: {}", from, to);

        List<VulnerabilityDto> result = new ArrayList<>();

        int page=0;
        VulBetweenDatesResponseDto response = null;
        do{
            log.info("Calling getPageOfVulnerabilitiesBetweenDates. Page: {}", page);
            try {
                response = getPageOfVulnerabilitiesBetweenDates(from, to, partnerToken, page);
                if (response != null && response.getNewVulnerabilities() != null) {

                    if (!findSha1s.isEmpty()) {

                        ResourceVulnerabilityDto dto = response
                                .getNewVulnerabilities()
                                .stream()
                                .flatMap(x -> x.getResourceVulnerabilities().stream())
                                .filter(x -> findSha1s.contains( x.getSha1() ) )
                                .findFirst()
                                .orElse(null);

                        if (dto != null) {
                            log.info("!!!Found resource vulnerability: {} (Sha1: {}) from: {}, to: {}, page: {}",
                                    dto,
                                    dto.getSha1(),
                                    from.format(ArgsDto.formatter),
                                    to.format(ArgsDto.formatter),
                                    page);
                        }
                    }

                    result.addAll(response.getNewVulnerabilities());
                }
            }
            catch (Exception e) {
                log.warn("Failed to read response: {} for page: {}", e.getMessage(), page);
            }

            page++;
        }
        while( response != null && response.getMetadata() != null && !response.getMetadata().isLastPage()  );

        log.info("<< getAllVulnerabilitiesBetweenDates found: {} vulnerabilities", result.size());
        return result;
    }


    private VulBetweenDatesResponseDto getPageOfVulnerabilitiesBetweenDates(LocalDate from, LocalDate to, String partnerToken, int page){

        VulBetweenDatesRequestDto request = new VulBetweenDatesRequestDto();
        request.setPartnerToken(partnerToken);
        request.setFrom(from.format(ArgsDto.formatter));
        request.setTo(to.format(ArgsDto.formatter));
        request.setPage(page);

        Mono<VulBetweenDatesResponseDto> mono = webClient.post()
                .uri(URI)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(VulBetweenDatesResponseDto.class);

        return mono.block();
    }


    @Async("taskExecutor")
    public CompletableFuture<Pair<ResourceVulnerabilityDto, VulElementResponseDto>> getAllVulnerableElements(ResourceVulnerabilityDto resourceVulnerability, String partnerToken){
        VulElementResponseDto response = getPageOfVulnerableElements(resourceVulnerability.getSha1(), partnerToken, 0);
        return CompletableFuture.completedFuture(Pair.of(resourceVulnerability, response));
    }

    private VulElementResponseDto getPageOfVulnerableElements(String sha1, String partnerToken, int page){
        VulElementRequestDto request = new VulElementRequestDto();
        request.setPartnerToken(partnerToken);
        request.setPage(page);
        request.addToHashes(sha1);

        Mono<VulElementResponseDto> mono = webClient.post()
                .uri(URI)
                .header("Accept", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(VulElementResponseDto.class);

        return mono.block();
    }



}
