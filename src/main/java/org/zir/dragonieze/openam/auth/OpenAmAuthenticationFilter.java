package org.zir.dragonieze.openam;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.zir.dragonieze.user.UserRepository;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Service
public class OpenAmAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private String openAmAuthUrl;
    private String openAmUserInfoUrl;

    private String openamRealm;

    private final String openAmCookieName = "iPlanetDirectoryPro";

    @Setter(onMethod_ = {@Autowired})
    private UserRepository userRepository;

    public static final String OPENAM_AUTH_URI = "/openam-auth";

    public OpenAmAuthenticationFilter() {
        super(OPENAM_AUTH_URI, new OpenAmAuthenticationManager());
        setSecurityContextRepository(new HttpSessionSecurityContextRepository());
    }

    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        Optional<Cookie> openamCookie = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(openAmCookieName)).findFirst();
        if (openamCookie.isEmpty()) {
            response.sendRedirect(getOpenAmAuthUrl(request));
            return null;
        } else {
            System.out.println("cookie found");
            String userId = getUserIdFromSession(openamCookie.get().getValue());
            if (userId == null) {
                throw new BadCredentialsException("invalid session!");
            }
            OpenAmAuthenticationToken token = new OpenAmAuthenticationToken(userId);
            token.setDetails(authenticationDetailsSource.buildDetails(request));
            return this.getAuthenticationManager().authenticate(token);
        }
    }

    protected String getUserIdFromSession(String sessionId) {
        RestTemplate restTemplate = new RestTemplate();
        ParameterizedTypeReference<Map<String, String>> responseType =
                new ParameterizedTypeReference<>() {
                };
        HttpHeaders headers = new HttpHeaders();
        headers.add(openAmCookieName, sessionId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(openAmUserInfoUrl, HttpMethod.POST, entity, responseType);
        Map<String, String> body = response.getBody();
        if (body == null) {
            return null;
        }
        return body.get("id");
    }

    @Value("${openam.auth.realm:/}")
    public void setOpenamRealm(String openamRealm) {
        this.openamRealm = openamRealm;
    }

    @Value("${openam.auth.url}")
    public void setOpenAmUrl(String openAmUrl) {
        this.openAmAuthUrl = openAmUrl.concat("/XUI/");
        this.openAmUserInfoUrl = openAmUrl.concat("/json/users?_action=idFromSession");
    }

    private String getFullRequestUrl(HttpServletRequest request) {
        StringBuilder result = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString != null) {
            result.append("?").append(queryString);
        }

        return result.toString();
    }

    public String getOpenAmAuthUrl(HttpServletRequest request) {
        String redirectUrl = getFullRequestUrl(request);
        return openAmAuthUrl + "?goto=" + URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8)
                + "&realm=" + URLEncoder.encode(openamRealm, StandardCharsets.UTF_8)
                + "#login";
    }
}
