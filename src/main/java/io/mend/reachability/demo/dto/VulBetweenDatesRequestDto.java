package io.mend.reachability.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

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
    private List<String> componentType = List.of( "MAVEN_ARTIFACT", "JAVA_ARCHIVE", "DOT_NET_RESOURCE", "DOT_NET_AS_GENERIC_RESOURCE", "PYTHON_PACKAGE", "NODE_PACKAGED_MODULE", "JAVA_SCRIPT_LIBRARY", "NUGET_PACKAGE_MODULE");
}
