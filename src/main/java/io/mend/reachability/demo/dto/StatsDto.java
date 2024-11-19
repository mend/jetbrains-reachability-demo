package io.mend.reachability.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatsDto {

    private int numOfDays = 0;
    public void incrementNumOfDays() { numOfDays++; }

    private int numOfVulnerabilities = 0;
    public void addToNumOfVulnerabilities(int addition) { numOfVulnerabilities += addition; }

    private int numOfResourceVulnerabilities = 0;
    public void addToNumOfResourceVulnerabilities(int addition) { numOfResourceVulnerabilities += addition; }

    private int numOfVulnerabilitiesWithRelevantElements = 0;
    public void incrementNumOfVulnerabilitiesWithRelevantElements() { numOfVulnerabilitiesWithRelevantElements++; }

    private int numOfElements = 0;
    public void addToNumOfElements(int addition) { numOfElements += addition; }

    private int numOfEngineExecutions = 0;
    public void incrementNumOfEngineExecutions() { numOfEngineExecutions++; }

    private int numOfEngineFailures = 0;
    public void incrementNumOfEngineFailures() { numOfEngineFailures++; }

}
