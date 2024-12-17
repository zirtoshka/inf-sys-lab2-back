package org.zir.dragonieze.openam.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

@AllArgsConstructor
public enum OpenAmRestApiMethod {
    AUTHENTICATE_USER(HttpMethod.POST, "/json/authenticate"),
    LOGOUT_USER(HttpMethod.POST, "/json/sessions/?_action=logout"),
    GET_USER_BY_COOKIE(HttpMethod.POST, "/json/users?_action=idFromSession&_fields=id,dn"),
    GET_USER(HttpMethod.GET, "/json/users/%s"),
    GET_USER_GROUPS(HttpMethod.GET, "/json/groups?_queryFilter=uniqueMember co \"%s\""),
    GET_GROUP_MEMBERS(HttpMethod.GET, "/json/groups/%s"),
    UPDATE_GROUP_MEMBERS(HttpMethod.PUT, "/json/groups/%s");

    @Getter
    private final HttpMethod httpMethod;

    private final String endpoint;

    public String getEndpointUrl(String apiBase, String ...params) {
        return apiBase + String.format(this.endpoint, (Object[]) params);
    }
}
