package io.mend.reachability.demo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProcessRunnerService {

  private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);

  //private Path workingDirectory;
  //private List<String> processArgs = new ArrayList<>();
  //private Consumer<String> consumer = new BlackHoleConsumer();
  //private long processTimeoutMs = 3600000;

  /**
   * Run a process with the given arguments and working directory.
   *
   * @param args The arguments to run the process with.
   * @param workingPath The working directory.
   * @return The exit code of the process.
   * @throws IOException If an I/O error occurs.
   * @throws InterruptedException If the current thread is interrupted.

  public static int runProcess(
      List<String> args, Path workingPath, Map<String, String> envVariables)
      throws IOException, InterruptedException {
    ProcessRunnerService runner = new ProcessRunnerService();
    runner.setProcessArgs(args);
    runner.setWorkingDirectory(workingPath);
    runner.setProcessTimeoutMs(120000);
    runner.setConsumer(log::info);
    ProcessRunnerConfig config =
        ProcessRunnerConfig.builder().getErrorOutput(true).envVariables(envVariables).build();

    ProcessRunnerService.ProcessRunnerResult result = runner.runSynchronously(config);

    return (result == null) ? -1 : (result.getCode() == null ? -1 : result.getCode());
  }*/

  public ProcessRunnerResult runSynchronously(ProcessRunnerConfig config)
      throws InterruptedException, IOException {
    log.info(">> runSynchronously: {}", config.getProcessArgs());

    var processRunnerResultBuilder = ProcessRunnerResult.builder();

    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.redirectErrorStream(true);
    Map<String, String> env = processBuilder.environment();
    env.put("PATH", System.getenv("PATH"));
    if (config.getEnvVariables() != null) {
      env.putAll(config.getEnvVariables());
    }

    processBuilder.command(config.getProcessArgs().toArray(new String[0]));
    processBuilder.directory(config.getWorkingDirectory().toFile());
    Process process = processBuilder.start();
    if (config.getConsumer() != null) {
      StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), config.getConsumer());
      EXECUTOR.submit(streamGobbler);
    }

    if (process.waitFor(config.getProcessTimeoutMs(), TimeUnit.MILLISECONDS)) {
      processRunnerResultBuilder.code(process.exitValue());
      if (config.isGetOutput()) {
        processRunnerResultBuilder.output(new String(process.getInputStream().readAllBytes()));
      }
      if (config.isGetErrorOutput()) {
        processRunnerResultBuilder.errorOutput(new String(process.getErrorStream().readAllBytes()));
      }
    }

    ProcessRunnerResult result = processRunnerResultBuilder.build();
    log.info("<< runSynchronously returns: {}", (result == null ? -1 : result.code));
    return result;
  }

  @Getter
  @Builder
  public static class ProcessRunnerConfig {
    private Path workingDirectory;
    @Builder.Default private List<String> processArgs = new ArrayList<>();
    @Builder.Default private Consumer<String> consumer = log::info;
    @Builder.Default private long processTimeoutMs = 3600000;
    @Builder.Default private boolean getErrorOutput = true;
    @Builder.Default private boolean getOutput = true;
    @Builder.Default Map<String, String> envVariables = new HashMap<>();
  }

  @Getter
  @Builder
  public static class ProcessRunnerResult {
    private Integer code;
    private String output;
    private String errorOutput;
  }

  private static class BlackHoleConsumer implements Consumer<String> {

    @Override
    public void accept(String s) {
      // Do nothing
    }
  }

  private record StreamGobbler(InputStream inputStream, Consumer<String> consumer)
      implements Runnable {

    @Override
    public void run() {
      new BufferedReader(new InputStreamReader(inputStream))
          .lines()
          .forEach(consumer == null ? new BlackHoleConsumer() : consumer);
    }
  }
}
