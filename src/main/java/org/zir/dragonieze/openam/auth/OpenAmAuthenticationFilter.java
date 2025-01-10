package org.zir.dragonieze.openam.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.zir.dragonieze.openam.api.OpenAmRestApiClient;
import org.zir.dragonieze.user.Role;
import org.zir.dragonieze.user.User;
import org.zir.dragonieze.user.UserRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

@Service
public class OpenAmAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private String openAmAuthUrl;
    private String openAmUserInfoUrl;

    private String openamRealm;

    public static final String OPENAM_COOKIE_NAME = "iPlanetDirectoryPro";

    @Setter(onMethod_ = {@Autowired})
    private UserRepository userRepository;
    @Setter(onMethod_ = {@Autowired})
    private OpenAmRestApiClient openAmApi;

    private String adminUsername;
    private String adminPassword;
    private String adminAuthCookie;

    public OpenAmAuthenticationFilter() {
        super("/**", new OpenAmAuthenticationManager());
        setAuthenticationSuccessHandler((request, response, authentication) -> {});
        setSecurityContextRepository(new HttpSessionSecurityContextRepository());
    }

    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        Cookie[] cookies = Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]);
        String authCookie = Arrays.stream(cookies)
                .filter(c -> c.getName().equals(OPENAM_COOKIE_NAME))
                .findFirst()
                .orElse(new Cookie(OPENAM_COOKIE_NAME, ""))
                .getValue();

        if (authCookie.isEmpty()) {
            throw new InvalidCookieException("iPlanetDirectoryPro cookie is missing");
        }

        try {
            User user = openAmApi.getUserByCookie(authCookie);
            if (user == null) {
                throw new BadCredentialsException("invalid session!");
            }
            Optional<User> foundUser;

            try {
                foundUser = userRepository.findByUsername(user.getUsername());
            } catch (Exception dbException) {
                System.out.println( "База данных недоступна:"+ dbException.getMessage());
                foundUser = Optional.empty();
            }

            if (foundUser.isEmpty()) {
                try {
                    userRepository.save(user);
                } catch (Exception saveException) {
                    System.out.println( "Не удалось сохранить пользователя в базу данных: "+ saveException.getMessage());
                    user = openAmApi.getUserByCookie(authCookie);
                }
            } else {
                user = foundUser.get();
            }

            String dn = user.getDn();
            String[] groups = execAsAdmin(cookie ->
                openAmApi.getUserGroups(cookie, dn)
            );

            Role[] roles = Arrays.stream(groups)
                    .map(Role::byFullName)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toArray(Role[]::new);

            if (!Arrays.asList(roles).contains(Role.USER)) {  //если новый пользователь нужно юзер роль дать
                execAsAdmin(cookie -> {
                    openAmApi.addUserToGroup(cookie, dn, Role.USER.getFullName());
                    return null;
                });
            }

            OpenAmAuthenticationToken token = new OpenAmAuthenticationToken(
                    new OpenAmUserPrincipal(user, authCookie, roles),
                    roles
            );
            token.setDetails(authenticationDetailsSource.buildDetails(request));
            return this.getAuthenticationManager().authenticate(token);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new InvalidCookieException("iPlanetDirectoryPro cookie has expired");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
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

    @Value("${admin.username}")
    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    @Value("${admin.password}")
    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    private void authenticateAdmin() {
        this.adminAuthCookie = openAmApi.authenticateUser(adminUsername, adminPassword);
    }

    private <T> T execAsAdmin(Function<String, T> fn) {
        try {
            return fn.apply(adminAuthCookie);
        } catch (HttpClientErrorException.Unauthorized e) {
            authenticateAdmin();
            return fn.apply(adminAuthCookie);
        }
    }
}
