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
public class MetaDataDto {

    private int page;
    private int pageSize;
    private boolean isLastPage;

    public void setIsLastPage(final boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

}
