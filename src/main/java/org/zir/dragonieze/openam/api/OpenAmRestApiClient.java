package org.zir.dragonieze.openam.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.zir.dragonieze.openam.api.models.*;
import org.zir.dragonieze.openam.auth.OpenAmAuthenticationFilter;
import org.zir.dragonieze.user.User;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class OpenAmRestApiClient {
    private String openAmUrl;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    @Value("${openam.auth.url}")
    private void setOpenAmUrl(String openAmUrl) {
        this.openAmUrl = openAmUrl;
    }

    private <Req, Resp> ResponseEntity<Resp>
        executeMethod(
                OpenAmRestApiMethod method,
                String[] urlParams,
                String authCookie,
                Req req,
                ParameterizedTypeReference<Resp> responseType
    ) {
        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        if (authCookie != null) {
            headers.add(OpenAmAuthenticationFilter.OPENAM_COOKIE_NAME, authCookie);
        }
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Req> request = new HttpEntity<>(req, headers);

        return template.exchange(
                method.getEndpointUrl(openAmUrl, urlParams),
                method.getHttpMethod(),
                request,
                responseType
        );
    }

    @Cacheable(value = "users", key = "#authCookie")
    public User getUserByCookie(String authCookie) {
        lock.readLock().lock();
        try {
            ResponseEntity<GetUserByCookieResponse> resp = executeMethod(
                    OpenAmRestApiMethod.GET_USER_BY_COOKIE,
                    new String[]{},
                    authCookie,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            if (!resp.hasBody()) {
                return null;
            }

            return getUser_(authCookie, resp.getBody().getUsername());
        } finally {
            lock.readLock().unlock();
        }
    }

    public User getUser(String authCookie, String username) {
        lock.readLock().lock();
        try {
            return getUser_(authCookie, username);
        } finally {
            lock.readLock().unlock();
        }
    }

    private User getUser_(String authCookie, String username) {
        ResponseEntity<GetUserResponse> resp = executeMethod(
                OpenAmRestApiMethod.GET_USER,
                new String[]{username},
                authCookie,
                null,
                new ParameterizedTypeReference<>() {}
        );

        if (!resp.hasBody()) {
            return null;
        }

        User user = new User();

        user.setUsername(resp.getBody().getUsername());
        user.setDn(resp.getBody().getDn()[0]);

        return user;
    }

    public String[] getUserGroups(String authCookie, String dn) {
        lock.readLock().lock();
        try {
            ResponseEntity<GetUserGroupsResponse> resp = executeMethod(
                    OpenAmRestApiMethod.GET_USER_GROUPS,
                    new String[]{dn},
                    authCookie,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            if (!resp.hasBody()) {
                return null;
            }

            return resp.getBody().getGroupNames();
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean isMemberOf(String authCookie, String dn, String groupName) {
        return Arrays.asList(getUserGroups(authCookie, dn)).contains(groupName);
    }

    private String[] getGroupMembers_(String authCookie, String groupName) {
        ResponseEntity<GetGroupMembersResponse> resp = executeMethod(
                OpenAmRestApiMethod.GET_GROUP_MEMBERS,
                new String[]{groupName},
                authCookie,
                null,
                new ParameterizedTypeReference<>() {}
        );

        if (!resp.hasBody()) {
            return null;
        }

        return resp.getBody().getMembers();
    }

    private void updateGroupMembers_(String authCookie, String groupName, String[] members) {
        executeMethod(
                OpenAmRestApiMethod.UPDATE_GROUP_MEMBERS,
                new String[]{groupName},
                authCookie,
                new UpdateGroupMembersRequest(groupName, members),
                new ParameterizedTypeReference<>() {}
        );
    }

    public void addUserToGroup(String authCookie, String dn, String groupName) {
        lock.writeLock().lock();
        try {
            String[] members = Optional.ofNullable(getGroupMembers_(authCookie, groupName))
                    .orElse(new String[]{});
            Set<String> membersSet = new HashSet<>(Arrays.asList(members));
            membersSet.add(dn);

            updateGroupMembers_(authCookie, groupName, membersSet.toArray(new String[]{}));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String authenticateUser(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-OpenAM-Username", username);
        headers.set("X-OpenAM-Password", password);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<AuthenticateUserResponse> resp = restTemplate.exchange(
                OpenAmRestApiMethod.AUTHENTICATE_USER.getEndpointUrl(openAmUrl),
                OpenAmRestApiMethod.AUTHENTICATE_USER.getHttpMethod(),
                request,
                new ParameterizedTypeReference<>() {}
        );

        return resp.getBody().getAuthCookie();
    }

    public void logoutUser(String authCookie) {
        executeMethod(
                OpenAmRestApiMethod.LOGOUT_USER,
                new String[]{},
                authCookie,
                null,
                new ParameterizedTypeReference<>() {}
        );
    }
}
