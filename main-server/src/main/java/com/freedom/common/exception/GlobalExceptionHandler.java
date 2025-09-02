package com.freedom.common.exception;

import com.freedom.common.exception.custom.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        log.warn("사용자를 찾을 수 없음: {}", e.getMessage());
        return createErrorResponse(ErrorCode.USER_NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException e) {
        log.warn("이메일 중복: {}", e.getMessage());
        return createErrorResponse(ErrorCode.DUPLICATE_EMAIL);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException e) {
        log.warn("비밀번호 불일치: {}", e.getMessage());
        return createErrorResponse(ErrorCode.INVALID_PASSWORD);
    }

    @ExceptionHandler(UserWithdrawnException.class)
    public ResponseEntity<ErrorResponse> handleUserWithdrawnException(UserWithdrawnException e) {
        log.warn("탈퇴한 사용자 접근: {}", e.getMessage());
        return createErrorResponse(ErrorCode.USER_WITHDRAWN);
    }

    @ExceptionHandler(UserSuspendedException.class)
    public ResponseEntity<ErrorResponse> handleUserSuspendedException(UserSuspendedException e) {
        log.warn("정지된 사용자 접근: {}", e.getMessage());
        return createErrorResponse(ErrorCode.USER_SUSPENDED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(TokenExpiredException e) {
        log.warn("토큰 만료: {}", e.getMessage());
        return createErrorResponse(ErrorCode.TOKEN_EXPIRED);
    }

    @ExceptionHandler(TokenInvalidException.class)
    public ResponseEntity<ErrorResponse> handleTokenInvalidException(TokenInvalidException e) {
        log.warn("유효하지 않은 토큰: {}", e.getMessage());
        return createErrorResponse(ErrorCode.TOKEN_INVALID);
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenExpiredException(RefreshTokenExpiredException e) {
        log.warn("리프레시 토큰 만료: {}", e.getMessage());
        return createErrorResponse(ErrorCode.REFRESH_TOKEN_EXPIRED);
    }

    @ExceptionHandler(RefreshTokenInvalidException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenInvalidException(RefreshTokenInvalidException e) {
        log.warn("유효하지 않은 리프레시 토큰: {}", e.getMessage());
        return createErrorResponse(ErrorCode.REFRESH_TOKEN_INVALID);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("유효성 검증 실패: {}", e.getBindingResult().getAllErrors());

        List<ValidationFieldError> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapToValidationFieldError)
                .toList();

        ValidationErrorResponse errorResponse = ValidationErrorResponse.of(ErrorCode.VALIDATION_ERROR, fieldErrors);
        return ResponseEntity
                .status(ErrorCode.VALIDATION_ERROR.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ValidationErrorResponse> handleBindException(BindException e) {
        log.warn("바인딩 예외: {}", e.getBindingResult().getAllErrors());

        List<ValidationFieldError> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapToValidationFieldError)
                .toList();

        ValidationErrorResponse errorResponse = ValidationErrorResponse.of(ErrorCode.VALIDATION_ERROR, fieldErrors);
        return ResponseEntity
                .status(ErrorCode.VALIDATION_ERROR.getStatus())
                .body(errorResponse);
    }

    /**
     * FieldError를 ValidationFieldError로 변환하는 메서드
     */
    private ValidationFieldError mapToValidationFieldError(FieldError fieldError) {
        return ValidationFieldError.of(
                fieldError.getField(),
                fieldError.getRejectedValue(),
                fieldError.getCode(),
                fieldError.getDefaultMessage()
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("지원하지 않는 HTTP 메서드: {}", e.getMessage());
        return createErrorResponse(ErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException e, HttpServletRequest request) {
        log.error("데이터베이스 접근 오류: {} {}", request.getMethod(), request.getRequestURI(), e);
        return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        log.error("데이터 무결성 위반: {} {}", request.getMethod(), request.getRequestURI(), e);
        return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception e, HttpServletRequest request) {
        log.error("예상하지 못한 예외 발생: {} {}", request.getMethod(), request.getRequestURI(), e);
        return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode) {
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(SavingProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSavingProductNotFoundException(SavingProductNotFoundException e) {
        log.warn("상품을 찾을 수 없음: {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.PRODUCT_NOT_FOUND.getStatus())
                .body(ErrorResponse.of(ErrorCode.PRODUCT_NOT_FOUND));

    }
}
