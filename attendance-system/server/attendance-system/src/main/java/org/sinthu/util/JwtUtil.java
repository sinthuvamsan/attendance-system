package org.sinthu.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token-validity}")
    private int tokenValidity;

    @Value("${jwt.algorithm}")
    private String algorithm;

    private SecretKey getSignInKey() {
        byte[] bytes = Base64.getDecoder()
                .decode(secretKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(bytes, algorithm); }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() +    tokenValidity))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean validateToken(String token, String username) {
        Claims claims = extractAllClaims(token);
        String usernameFromToken = claims.getSubject();
        return (usernameFromToken.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}