package com.buzz.security;

import com.buzz.auth.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Authorization header'ını al
        final String authHeader = request.getHeader("Authorization");

        // 2. Token yoksa veya "Bearer " ile başlamıyorsa geç
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " kısmını kes, sadece token'ı al
        final String jwt = authHeader.substring(7);

        // 4. Token'dan username'i çıkar
        final String username = jwtService.extractUsername(jwt);

        // 5. Username varsa ve henüz authenticate olmamışsa
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Veritabanından kullanıcıyı bul
            var userDetails = this.userDetailsService.loadUserByUsername(username);

            // 7. Token geçerli mi kontrol et
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 8. Authentication token oluştur
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 9. Security context'e kaydet
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 10. Sonraki filtreye geç
        filterChain.doFilter(request, response);
    }
}