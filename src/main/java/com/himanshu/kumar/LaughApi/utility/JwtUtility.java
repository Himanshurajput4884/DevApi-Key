package com.himanshu.kumar.LaughApi.utility;

import com.himanshu.kumar.LaughApi.configuration.TokenConfigurationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
@EnableConfigurationProperties(TokenConfigurationProperties.class)
public class JwtUtility {

    private static final String BEARER_PREFIX = "Bearer ";

    private final String issuer;
    private final TokenConfigurationProperties tokenConfigurationProperties;

   public JwtUtility(@Value("${spring.application.name}") final String issuer,
                     final TokenConfigurationProperties tokenConfigurationProperties) {
       this.issuer = issuer;
       this.tokenConfigurationProperties = tokenConfigurationProperties;
   }

    public String generateAccessToken(@NonNull final UUID userId) {
       final var audience = String.valueOf(userId);

       final var accessTokenValidity = tokenConfigurationProperties.getTokenValidityMinutes();
       final var expiration = TimeUnit.MINUTES.toMillis(accessTokenValidity);
       final var currentTimeStamp = new Date(System.currentTimeMillis());
       final var expirationTimeStamp = new Date(System.currentTimeMillis() + expiration);

       final var encodedSecretKey = tokenConfigurationProperties.getSecretKey();
       final var secretKey = getSecretKey(encodedSecretKey);

       return Jwts.builder()
               .issuer(issuer)
               .issuedAt(currentTimeStamp)
               .expiration(expirationTimeStamp)
               .audience().add(audience)
               .and()
               .signWith(secretKey, Jwts.SIG.HS256)
               .compact();
    }

    /**
     * Extracts user's ID from a given JWT token signature an authenticated
     *
     * @param token
     * @return
     */
    public UUID getUserId(@NonNull final String token) {
       final var audience = extractClaim(token, Claims::getAudience).iterator().next();
       return UUID.fromString(audience);
    }

    /**
     * Extracts a specific claim from the provided JWT token. This method verifies
     * the token's issuer and signature before extracting the class.
     * @param token
     * @param claimsResolver
     * @return
     * @param <T>
     */
    private <T> T extractClaim(@NonNull final String token, @NonNull final Function<Claims, T> claimsResolver) {
        final var encodedSecretKey = tokenConfigurationProperties.getSecretKey();
        final var secretKey = getSecretKey(encodedSecretKey);
        final var sanitizedToken = token.replace(BEARER_PREFIX, "");
        final var claims = Jwts.parser()
                .requireIssuer(issuer)
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(sanitizedToken)
                .getPayload();
        return claimsResolver.apply(claims);
    }


    /**
     * Construct an instance of secret key from the provided Base64-encoded
     * @param encodedSecretKey
     * @return
     */
    private SecretKey getSecretKey(@NonNull final String encodedSecretKey) {
        final var decodedSecretKey = Decoders.BASE64.decode(encodedSecretKey);
        return Keys.hmacShaKeyFor(decodedSecretKey);
    }
}
