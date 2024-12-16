package org.zir.dragonieze.openam.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticateUserResponse {
    @JsonProperty("tokenId")
    String authCookie;
}
