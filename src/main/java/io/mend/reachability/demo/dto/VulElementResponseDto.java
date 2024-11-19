package io.mend.reachability.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class VulElementResponseDto {

    private MetaDataDto metadata;
    private Map<String, List<Sha1ToVulnerableElementDto>> sha1ToVulnerableElements;
    private String requestToken;

}
