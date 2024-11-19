package io.mend.reachability.demo;

import io.mend.reachability.demo.dto.ArgsDto;
import io.mend.reachability.demo.dto.StatsDto;
import io.mend.reachability.demo.service.ReachabilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@SpringBootApplication
@Slf4j
public class DemoApplication implements CommandLineRunner {

	private final ReachabilityService reachabilityService;

	public DemoApplication(ReachabilityService reachabilityService) {
		this.reachabilityService = reachabilityService;
	}

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(DemoApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
	}

	@Override
	public void run(String... args) throws Exception {

		try {
			//Parse the arguments
			ArgsDto arguments = getAndValidateArgs(args);
			log.info("Arguments: {}", arguments);

			//Fetch the data
			StatsDto statsDto = reachabilityService.fetchReachabilityData(arguments);
			log.info("Reachability data fetched: {}", statsDto);
		}
		catch (IllegalArgumentException e){
			log.error("IllegalArgumentException: {}", e.getMessage());
			log.info("{}", getArgumentMessage());
		}
		catch (Exception e) {
			log.error(e.getMessage());
			System.exit(-1);
		}

		System.exit(0);

	}

	private ArgsDto getAndValidateArgs(String... args){
		ArgsDto result = new ArgsDto();

		for(int currentIndex = 0; currentIndex < args.length; currentIndex++){

			String currentArg = args[currentIndex];
			if (currentArg.equals("--fromDate")){
				String fromDateStr = args[++currentIndex];
				try{
					result.setFromDate( LocalDate.parse(fromDateStr, ArgsDto.formatter) );
				}
				catch (Exception e) {
					log.error("Failed to read fromDate: {}. Use the following format: yyyy-MM-dd", fromDateStr);
					throw new IllegalArgumentException("Invalid fromDate format");
				}
			}

			else if (currentArg.equals("--toDate")){
				try{
					result.setToDate( LocalDate.parse(args[++currentIndex], ArgsDto.formatter) );
				}
				catch (Exception e) {
					log.error("Failed to read toDate. Use the following format: yyyy-MM-dd");
					throw new IllegalArgumentException("Invalid toDate format");
				}
			}

			else if (currentArg.equals("--enginePath")){
				Path mendExecPath = Path.of(args[++currentIndex]);
				if (!mendExecPath.toFile().exists()) {
					log.error("Failed to find the mend executable file: {}", mendExecPath);
					throw new IllegalArgumentException("Failed to find the mend executable file");
				}
				result.setMendExecPath(mendExecPath.toAbsolutePath().normalize());
			}

			else if (currentArg.equals("--sourceRootPath")){
				Path srcRootPath = Path.of(args[++currentIndex]);
				if (!srcRootPath.toFile().exists()) {
					log.error("Failed to find the source root directory: {}", srcRootPath);
					throw new IllegalArgumentException("Failed to find the source root directory.");
				}
				result.setSrcRootPath(srcRootPath.toAbsolutePath().normalize());
			}


			else if (currentArg.equals("--outputPath")){
				Path outputPath = Path.of(args[++currentIndex]);
				if (!outputPath.toFile().exists() && !outputPath.toFile().mkdirs()) {
					log.error("Failed to create output directory: {}", outputPath);
					throw new IllegalArgumentException("Failed to create output directory.");
				}
				result.setOutputPath(outputPath.toAbsolutePath().normalize());
			}

			else if (currentArg.equals("--dont-scan")){
				result.setScan(false);
			}

			else if (currentArg.equals("--partnerToken")){
				result.setPartnerToken( args[++currentIndex] );
			}

			if (result.getToDate() == null){
				result.setToDate( result.getFromDate().plusDays(1) );
			}
		}



		if (!result.validate()){
			throw new IllegalArgumentException("Invalid arguments");
		}

		return result;
	}

	private String getArgumentMessage(){

		String NEW_LINE = "\r\n";

		StringBuilder sb = new StringBuilder();

		sb
				.append(NEW_LINE)
				.append("Argument list: --fromDate [date] --toDate [date] --outputPath [path] --dont-scan --enginePath [path] --sourceRootPath [path] --partnerToken [token]").append(NEW_LINE)
				.append("Where:").append(NEW_LINE)
				.append("--fromDate: The date from where to start query the DB in the following format: yyyy-MM-dd. Mandatory").append(NEW_LINE)
				.append("--toDate: The date from where to stop query the DB in the following format: yyyy-MM-dd. Optional. Default: fromDate + 1 day").append(NEW_LINE)
				.append("--outputPath: The folder into which the result will be written. Mandatory").append(NEW_LINE)
				.append("--dont-scan: Use this flag to skip the scan phase. Optional. Default: false").append(NEW_LINE)
				.append("--enginePath: The full path to the mend executable engine. Mandatory if --dont-scan==false").append(NEW_LINE)
				.append("--sourceRootPath: The path to the root of the source dir. Mandatory if --dont-scan==false").append(NEW_LINE)
				.append("--partnerToken: The API partner token as received from Mend. Mandatory.").append(NEW_LINE)
				.append(NEW_LINE)
				.append("Example: --fromDate 2018-07-01 --toDate 2018-07-02  --outputPath /home/user/reachability/output --enginePath /home/user/engine --sourceRootPath /home/user/reachability/sources --partnerToken dummy-token").append(NEW_LINE)
				.append("Example: --fromDate 2018-07-01 --toDate 2018-07-02  --outputPath /home/user/reachability/output --dont-scan --partnerToken dummy-token").append(NEW_LINE);

		return sb.toString();
	}
}
