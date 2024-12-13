package org.zir.dragonieze;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/dragon/am")
public class OpenAuthController {


    @GetMapping("/status")
    public ResponseEntity<String> checkAuthStatus(HttpServletRequest request) {
        System.out.println("checkAuthStatus");
        String sessionCookie = getSessionCookie(request);
        if (sessionCookie != null && isSessionValid(sessionCookie)) {
            return ResponseEntity.ok("Authenticated");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }



    private String getSessionCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("iPlanetDirectoryPro".equals(cookie.getName())) { //JSESSIONID iPlanetDirectoryPro
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    private boolean isSessionValid(String sessionCookie) {
        // Здесь вы можете проверить сессию с OpenAM с помощью их API.
        // Для простоты это может быть запрос к серверу OpenAM для проверки сессии.
        // Пример API-запроса к OpenAM для проверки сессии
        boolean valid = checkSessionWithOpenAM(sessionCookie);
        return valid;
    }

    private boolean checkSessionWithOpenAM(String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "iPlanetDirectoryPro=" + sessionCookie);//iPlanetDirectoryPro  JSESSIONID
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://openam.example.com/openam/session",
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }


}
