package io.mend.reachability.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class VulElementRequestDto {

    private final String requestType = "getVulnerableElements";
    private String partnerToken;
    private int page;
    private int pageSize = 50;
    Set<String> hashes = new HashSet<>();

    public void addToHashes(String hash) {
        hashes.add(hash);
    }

}
