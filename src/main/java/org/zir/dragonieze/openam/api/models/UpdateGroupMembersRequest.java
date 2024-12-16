package org.zir.dragonieze.openam.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupMembersRequest {
    @JsonProperty("_id")
    String username;
    @JsonProperty("uniqueMember")
    String[] members;
}
