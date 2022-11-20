package com.example.childrenhabitsserver.auth;

import com.example.childrenhabitsserver.entity.CustomUserDetails;
import com.example.childrenhabitsserver.model.JWTTokenModelResponse;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {


    // Đoạn JWT_SECRET này là bí mật, chỉ có phía server biết
    private final String JWT_SECRET = "doubleh";

    //Thời gian có hiệu lực của chuỗi jwt
//    private final long JWT_EXPIRATION = 604800000L;
    private final int JWT_EXPIRATION_HOUR = 1;

    // Tạo ra jwt từ thông tin user
    public JWTTokenModelResponse generateToken(CustomUserDetails userDetails) {
        Date now = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.add(Calendar.HOUR, JWT_EXPIRATION_HOUR);
        Date expiryDate = c.getTime();


        // Tạo chuỗi json web token từ id của user.
        String jwtToken = Jwts.builder()
                .setSubject(userDetails.getUser().getId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
        JWTTokenModelResponse jwtTokenModelResponse = JWTTokenModelResponse.builder()
                .jwtToken(jwtToken)
                .userId(userDetails.getUser().getId())
                .expirationJWTDate(expiryDate)
                .build();
        return jwtTokenModelResponse;
    }

    // Lấy thông tin user từ jwt
    public String getUserIdFromJWT(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(JWT_SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            if (claims.getExpiration().before(new Date())) {
                throw new JwtException("Expired JWT token");
            }
            return claims.getSubject();
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        } catch (JwtException ex) {
            log.error("Expired JWT token");
        }
        return null;
    }

    public boolean validateToken(String authToken) {
        try {
            Jws jws = Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            JwtParser jwtParser = Jwts.parser().setSigningKey(JWT_SECRET);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        } catch (JwtException ex) {
            log.error("Expired JWT token");
        }
        return false;
    }
}
