package com.example.childrenhabitsserver.auth;

import com.example.childrenhabitsserver.common.constant.ErrorCodeService;
import com.example.childrenhabitsserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
//public class JwtAuthenticationFilter extends RequestContextFilter {
    private final JwtTokenProvider tokenProvider;
    private final UserService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider,
                                   UserService customUserDetailsService){
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                                    HttpServletResponse response, FilterChain filterChain) {
//        try {
            // Lấy jwt từ request
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                logger.info("jwt is " + jwt);
                // Lấy id user từ chuỗi jwt
                String userId = tokenProvider.getUserIdFromJWT(jwt);
                // Lấy thông tin người dùng từ id
                UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                if (userDetails != null && userDetails.isCredentialsNonExpired()) {
                    // Nếu người dùng hợp lệ, set thông tin cho Seturity Context
                    UsernamePasswordAuthenticationToken
                            authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                else {
                    throw new AccessDeniedException(ErrorCodeService.UN_AUTH);
                }
            }
//            else {
//                throw new JwtException(ErrorCodeService.UN_AUTH);
//            }
            try {
                filterChain.doFilter(request, response);
            } catch (Exception exception) {
                exception.printStackTrace();
                throw new AccessDeniedException(ErrorCodeService.UN_AUTH);
            }

//        } catch (Exception ex) {
//            log.error("failed on set user authentication {}", ex);
//            throw new AccessDeniedException(ErrorCodeService.UN_AUTH);
//        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Kiểm tra xem header Authorization có chứa thông tin jwt không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
