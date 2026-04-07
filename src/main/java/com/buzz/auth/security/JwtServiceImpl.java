package com.buzz.auth.security;

import com.buzz.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${buzz.jwt.secret}")
    private String secretKey;

    @Value("${buzz.jwt.expiration}")
    private long jwtExpiration;

    // ===== Public Interface Metodları =====

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
        // Artık aslında email dönüyor
        // Metot ismi "extractUsername" ama subject'te email var
        // Spring convention'ı böyle, kafa karıştırmasın
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractUsername(token);   // token'dan email
        User user = (User) userDetails;                // cast
        return (email.equals(user.getEmail())) && !isTokenExpired(token);
    }

    // ===== Private Yardımcı Metodlar =====

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        User user = (User) userDetails;                // cast

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getEmail())              // ✅ EMAIL
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}