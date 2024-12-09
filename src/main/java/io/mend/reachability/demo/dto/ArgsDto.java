package io.mend.reachability.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArgsDto {

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LocalDate fromDate = null;
    private LocalDate toDate = null;
    private Path mendExecPath = null;
    private Path srcRootPath = null;
    private Path outputPath = null;
    private boolean scan = true;
    private boolean downloadSources = false;
    private String partnerToken = null;


    public boolean validate(){
        if (fromDate == null){
            log.error("--fromDate parameter cannot be null" );
            return false;
        }

        if (toDate == null){
            log.error("--fromDate parameter cannot be null" );
            return false;
        }

        if (outputPath == null){
            log.error("--outputPath parameter cannot be null" );
            return false;
        }

        if (scan) {
            if (mendExecPath == null){
                log.error("--mendExecPath parameter cannot be null or set the --dontScan parameter to true" );
                return false;
            }

            if (srcRootPath == null){
                log.error("--srcRootPath parameter cannot be null or set the --dontScan parameter to true" );
                return false;
            }
        }

        if (partnerToken == null){
            log.error("--partnerToken parameter cannot be null" );
            return false;
        }

        return true;
    }

}
