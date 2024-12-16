package org.zir.dragonieze.openam.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetUserGroupsResponse {
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultModel {
        @JsonProperty("username")
        private String name;
    }

    @JsonProperty("result")
    private ResultModel[] result;

    public String[] getGroupNames() {
        return Arrays.stream(result)
                .map(r -> r.name)
                .toArray(String[]::new);
    }
}
