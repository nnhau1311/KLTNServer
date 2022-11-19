package com.example.childrenhabitsserver.controller;

import com.example.childrenhabitsserver.auth.JwtTokenProvider;
import com.example.childrenhabitsserver.base.response.WrapResponse;
import com.example.childrenhabitsserver.common.request.LoginRequest;
import com.example.childrenhabitsserver.common.response.LoginResponse;
import com.example.childrenhabitsserver.entity.CustomUserDetails;
import com.example.childrenhabitsserver.entity.UserCustomStorage;
import com.example.childrenhabitsserver.model.JWTTokenModelResponse;
import com.example.childrenhabitsserver.service.UserCustomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserCustomService userCustomService;

    @PostMapping("/login")
    public WrapResponse<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // Xác thực từ username và password.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Nếu không xảy ra exception tức là thông tin hợp lệ
        // Set thông tin authentication vào Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Trả về jwt model cho người dùng.
        JWTTokenModelResponse jwtTokenModelResponse = tokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());

        UserCustomStorage user = userCustomService.updateExpirationJWTDate(jwtTokenModelResponse.getUserId(), jwtTokenModelResponse.getExpirationJWTDate());
//        return new LoginResponse(jwt,"Bearer");
        return WrapResponse.ok(new LoginResponse(jwtTokenModelResponse.getJwtToken(), "Bearer"));
    }

    @PostMapping("/logout")
    public WrapResponse<Object> authenticateUserLogOut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
//        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
//        request.logout();
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(null);
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error(">>>>>>>>>>>>>> clearContext error :", e);
        }
//        return "Done!";
        return WrapResponse.ok("Done!");
    }
}
