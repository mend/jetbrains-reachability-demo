package io.mend.reachability.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.nio.file.Path;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileStructure {

    private final Path outputPath;
    private final Path relativePath;
    private final Path srcRootPath;

    public Path getSrcPath() {
        return getSrcRootPath().resolve(getRelativePath()).toAbsolutePath().normalize();
    }

    public Path getRequestPath(){
        return getOutputPath().resolve(getRelativePath()).resolve("request.json").toAbsolutePath().normalize();
    }

    public Path getResponsePath(){
        return getOutputPath().resolve(getRelativePath()).resolve("response.json").toAbsolutePath().normalize();
    }

    public Path getFullResultPath(){
        return getOutputPath().resolve(getRelativePath()).resolve("result").resolve("full").toAbsolutePath().normalize();
    }

    public Path getVulResultPath(){
        return getOutputPath().resolve(getRelativePath()).resolve("result").resolve("vul").toAbsolutePath().normalize();
    }

    public Path getWorkingPath(){
        return getOutputPath().resolve(getRelativePath()).toAbsolutePath().normalize();
    }

    public Path getFullResultSaveIdPath(){
        return getFullResultPath().resolve("save-id.json").toAbsolutePath().normalize();
    }

    public Path getVulResultSaveIdPath(){
        return getVulResultPath().resolve("save-id.json").toAbsolutePath().normalize();
    }


}
