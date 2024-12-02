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
public class VulnerableEntity {

    private String element;
    private String namespace;
    private String className;
    private String method;
    private int startLine;
    private int endLine;
    private String language;
    private String type;

    public boolean isEmpty(){
        return element.equals("CrossLanguage.CrossLanguage:CrossLanguage") &&
        namespace.equals("CrossLanguage") &&
        className.equals("CrossLanguage") &&
        method.equals("CrossLanguage");
    }

    public boolean isNotEmpty(){
        return !isEmpty();
    }

}
