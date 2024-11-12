package org.zir.dragonieze.auth;

import lombok.SneakyThrows;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;


import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private static final String SECRET = "dragonieze0914a072a1dd67d2bcea47d5764a5653deeb39880c6d08d3bbd8ced116d63be8";

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";

    @SneakyThrows
    public String extractUsername(String token) {
        System.out.println("gogooggo " + token);
        return extractClaim(token, claims -> {
            try {
                return claims.getSubject();
            } catch (MalformedClaimException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <T> T extractClaim(String token, Function<JwtClaims, T> claimsResolver) throws InvalidJwtException {
        final JwtClaims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Key getKey() {
        return new HmacKey(SECRET.getBytes());
    }

    public String generateToken(UserDetails userDetails) throws JoseException {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) throws JoseException {
        JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.setSubject(userDetails.getUsername());
        jwtClaims.setIssuedAtToNow();
        jwtClaims.setExpirationTimeMinutesInTheFuture(60); // токен активен 1 час
        jwtClaims.setClaim("role", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(jwtClaims.toJson());
        jws.setKey(getKey());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256); //RSA_USING_SHA256

        return jws.getCompactSerialization();
    }

    public JwtClaims validateToken(String token) {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireIssuedAt()
                .setVerificationKey(getKey())
                .build();

        try {
            return jwtConsumer.processToClaims(token);
        } catch (InvalidJwtException e) {
            //todo
            System.out.println("Token validation failed: " + e.getMessage());
            return null;
        }
    }

    public boolean isTokenExpired(JwtClaims jwtClaims) throws MalformedClaimException {
        return jwtClaims.getExpirationTime().isBefore(NumericDate.now());

    }

    private JwtClaims extractAllClaims(String token) throws InvalidJwtException {
        System.out.println("agagagga " + token);
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setMaxFutureValidityInMinutes(300)
                .setRequireSubject()
                .setVerificationKey(getKey())
                .build();
        System.out.println(jwtConsumer.processToClaims(token));
        return jwtConsumer.processToClaims(token);
    }


    public List<String> getRolesFromClaims(JwtClaims claims) {
        Object roleClaim = claims.getClaimValue("role");
        if (roleClaim instanceof List<?>) {
            return (List<String>) roleClaim;
        }
        return List.of();
    }
}
