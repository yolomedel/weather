package com.weatherData.weather.security.jwt;

import java.security.Key;
import java.util.Date;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.weatherData.weather.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${weather.app.jwtSecret}")
  private String jwtSecret;

  @Value("${weather.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  @Value("${weather.app.jwtCookieName}")
  private String jwtCookie;

  private final boolean isSecure = false;

  /**
   * Extract JWT from cookies
   */
  public String getJwtFromCookies(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, jwtCookie);
    return (cookie != null) ? cookie.getValue() : null;
  }

  /**
   * Create a JWT cookie to store in the response
   */
  public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername()
        );



        return ResponseCookie.from(jwtCookie, jwt)
                .path("/")
                .httpOnly(true)
                .secure(isSecure)             // Für lokale Entwicklung false, sonst true
                .sameSite("Lax")              // Lax ist weniger restriktiv als Strict
                .maxAge(jwtExpirationMs / 1000)
                .build();
    }

  /**
   * Clear the JWT cookie (logout).
   */
  public ResponseCookie getCleanJwtCookie() {
      return ResponseCookie.from(jwtCookie, "")
              .path("/")
              .maxAge(0)
              .httpOnly(true)
              .secure(isSecure)
              .sameSite("Lax")
              .build();
  }

  /**
   * Parse username from JWT token.
   */
  public String getUserNameFromJwtToken(String token) {
      return Jwts.parserBuilder()
              .setSigningKey(getSigningKey())
              .build()
              .parseClaimsJws(token)
              .getBody()
              .getSubject();
  }

  /**
   * Validate JWT token.
   */
  public boolean validateJwtToken(String authToken) {
      try {
          Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
          return true;
      } catch (SecurityException | MalformedJwtException e) {
          logger.error("Invalid JWT signature: {}", e.getMessage());
      } catch (ExpiredJwtException e) {
          logger.error("JWT token is expired: {}", e.getMessage());
      } catch (UnsupportedJwtException e) {
          logger.error("JWT token is unsupported: {}", e.getMessage());
      } catch (IllegalArgumentException e) {
          logger.error("JWT claims string is empty: {}", e.getMessage());
      }

      return false;
  }

  /**
   * Generate token string from username.
   */
  public String generateTokenFromUsername(String username) {
      return Jwts.builder()
              .setSubject(username)
              .setIssuedAt(new Date())
              .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
              .signWith(getSigningKey(), SignatureAlgorithm.HS256)
              .compact();
  }

  /**
   * Decode Base64 secret key.
   */
  private Key getSigningKey() {
      byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
      return Keys.hmacShaKeyFor(keyBytes);
  }
}
