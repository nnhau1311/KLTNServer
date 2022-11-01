package com.example.childrenhabitsserver.config;
import com.example.childrenhabitsserver.base.BaseException;
import com.example.childrenhabitsserver.base.BaseObjectLoggable;
import com.example.childrenhabitsserver.base.exception.AccessDeniedException;
import com.example.childrenhabitsserver.base.exception.ServiceException;
import com.example.childrenhabitsserver.base.response.WrapResponse;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalWebExceptionHandle extends BaseObjectLoggable {

    @ExceptionHandler(BaseException.class)
    @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
    public WrapResponse<Object> handleBaseExceptions(BaseException baseException) {
        baseException.printStackTrace();
        WrapResponse baseResponse = new WrapResponse();
        baseResponse.setSuccess(false);
        baseResponse.setStatusCode("422");
        baseResponse.setErrorCode(baseException.getErrorCode());
        baseResponse.setMessage(Arrays.asList(baseException.getMessage()));
//        return WrapResponse.error(baseException.getMessage());
        return baseResponse;
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
    public WrapResponse<Object> handleServiceExceptions(ServiceException serviceException) {
        serviceException.printStackTrace();
        WrapResponse baseResponse = new WrapResponse();
        baseResponse.setSuccess(false);
        baseResponse.setStatusCode("505");
        baseResponse.setErrorCode(serviceException.getErrorCode());
        baseResponse.setMessage(Arrays.asList(serviceException.getMessage()));
//        return WrapResponse.error(serviceException.getMessage());
        return baseResponse;
    }
//    @ExceptionHandler(BlockedUserException.class)
//    @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
//    public ResponseEntity<Object> handleBlockedExceptions(BaseException baseException) {
//        baseException.printStackTrace();
//        WrapResponse baseResponse = new WrapResponse();
//        baseResponse.setSuccess(false);
//        baseResponse.setStatusCode("403");
//        baseResponse.setErrorCode(baseException.getErrorCode());
//        baseResponse.setMessage(Arrays.asList(baseException.getMessage()));
//        return ResponseEntity.status(HttpStatus.FORBIDDEN.value())
//                .body(baseResponse);
//    }
//
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseEntity<Object> handleNotValidExceptions(MethodArgumentNotValidException ex) {
//        WrapResponse baseResponse = new WrapResponse();
//        baseResponse.setSuccess(false);
//        baseResponse.setStatusCode("400");
//        baseResponse.setErrorCode(SharedErrorCode.BadRequest);
//        if (ex.getBindingResult() != null && ex.getBindingResult().getFieldErrors() != null) {
//            List<Violation> lstViolations = new ArrayList<>();
//            ex.getBindingResult().getFieldErrors().forEach(item -> {
//                lstViolations.add(Violation.builder()
//                        .field(item.getField())
//                        .errorCode(item.getDefaultMessage())
//                        .message(MessageSourceUtils.getMessage(item.getDefaultMessage()))
//                        .build());
//            });
//            baseResponse.setData(lstViolations);
//        }
//
//        baseResponse.setMessage(ex.getBindingResult().getAllErrors().stream()
//                .map(error -> MessageSourceUtils.getMessage(error.getDefaultMessage()))
//                .collect(Collectors.toList()));
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
//                .body(baseResponse);
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseEntity<Object> handleViolationExceptions(ConstraintViolationException ex) {
//        WrapResponse baseResponse = new WrapResponse();
//        baseResponse.setSuccess(false);
//        baseResponse.setStatusCode("400");
//        baseResponse.setViolations(true);
//        baseResponse.setErrorCode(SharedErrorCode.BadRequest);
//
//        baseResponse.setData(ex.getLstViolations());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
//                .body(baseResponse);
//    }
//
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    public WrapResponse<Object> handleAccessDeniedException(BadCredentialsException ex) {
        WrapResponse baseResponse = new WrapResponse();
        baseResponse.setSuccess(false);
        baseResponse.setStatusCode("410");
        baseResponse.setErrorCode(ex.getMessage());
//        List<String> errorMsg = new ArrayList<>();
//        errorMsg.add(MessageSourceUtils.getMessagePermissions(ex.getMessage()));
        baseResponse.setMessage(Arrays.asList("Thông tin đăng nhập không hợp lệ"));
//        return WrapResponse.error(ex.getMessage());
        return baseResponse;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
//    public ResponseEntity<Object> handleAccessDeniedException(Exception ex) {
    public WrapResponse<Object> handleAccessDeniedException(AccessDeniedException ex) {
        WrapResponse baseResponse = new WrapResponse();
        baseResponse.setSuccess(false);
        baseResponse.setStatusCode("403");
        baseResponse.setErrorCode("Không có xác thực");

        baseResponse.setMessage(Arrays.asList(ex.getMessage()));
//        return ResponseEntity.status(HttpStatus.FORBIDDEN.value())
//                .body(baseResponse);
//        return WrapResponse.error(ex.getMessage());
        return baseResponse;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public WrapResponse<Object> handleExceptions(Exception ex) {
//        if (ex.getClass().getName().equals("io.eventuate.EventuateCommandProcessingFailedException")
//                && ex.getCause() instanceof BaseException
//        ) {
//            return handleBaseExceptions((BaseException) ex.getCause());
//        }
        ex.printStackTrace();
        WrapResponse baseResponse = new WrapResponse();
        baseResponse.setSuccess(false);
        baseResponse.setStatusCode("500");
        baseResponse.setErrorCode("Hệ thống không thể xử lý");
        baseResponse.setMessage(Arrays.asList(ex.getMessage()));
        return baseResponse;
    }

    @ExceptionHandler({MalformedJwtException.class, JwtException.class,
            UnsupportedJwtException.class,IllegalArgumentException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public WrapResponse<Object> JWTExceptionHandler(Exception ex, WebRequest request) {
        WrapResponse baseResponse = new WrapResponse();
        baseResponse.setSuccess(false);
        baseResponse.setStatusCode("403");
        baseResponse.setErrorCode("Hệ thống không thể xử lý");
        baseResponse.setMessage(Arrays.asList(ex.getMessage()));
        return baseResponse;
    }
}
