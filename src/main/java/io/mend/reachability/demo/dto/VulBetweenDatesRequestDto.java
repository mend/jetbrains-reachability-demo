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
public class VulBetweenDatesRequestDto {

    private final String requestType = "getVulnerabilitiesBetweenDates";
    private String partnerToken;
    private int page;
    private int pageSize = 50;
    private boolean delete = false;

    private String from;
    private String to;

}
