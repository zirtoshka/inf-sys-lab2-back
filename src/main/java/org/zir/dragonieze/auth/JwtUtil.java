package org.zir.dragonieze.auth;
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

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private static final String SECRET = "dragonieze0914a072a1dd67d2bcea47d5764a5653deeb39880c6d08d3bbd8ced116d63be8";

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";

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
        jwtClaims.setIssuer("https://draconieza.com");
        extraClaims.forEach(jwtClaims::setClaim);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(jwtClaims.toJson());
        jws.setKey(getKey());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256); //RSA_USING_SHA256

        return jws.getCompactSerialization();
    }

    public JwtClaims validateToken(String token)  {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireIssuedAt()
                .setVerificationKey(getKey())
                .setExpectedIssuer("https://draconieza.com")
//                .setExpectedSubject(username??)
                .build();

        try {
            return jwtConsumer.processToClaims(token);
        } catch (InvalidJwtException e) {
           return null;
        }
    }

    public boolean isTokenExpired(JwtClaims jwtClaims) throws MalformedClaimException {
        return jwtClaims.getExpirationTime().isBefore(NumericDate.now());

    }
}
