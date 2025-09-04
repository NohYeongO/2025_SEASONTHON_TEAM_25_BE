package com.freedom.common.exception;

import com.freedom.common.exception.custom.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
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
  
    @ExceptionHandler(NewsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNewsNotFoundException(NewsNotFoundException e) {
        log.warn("뉴스를 찾을 수 없음: {}", e.getMessage());
        return createErrorResponse(ErrorCode.NEWS_NOT_FOUND);
    }

    @ExceptionHandler(QuizNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleQuizNotFoundException(QuizNotFoundException e) {
        log.warn("퀴즈를 찾을 수 없음: {}", e.getMessage());
        return createErrorResponse(ErrorCode.QUIZ_NOT_FOUND);
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

    // ===== saving mapping =====
    @ExceptionHandler(SavingExceptions.SavingSubscriptionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSavingSubscriptionNotFound() {
        return createErrorResponse(ErrorCode.SAVING_SUBSCRIPTION_NOT_FOUND);
    }

    @ExceptionHandler(SavingExceptions.SavingSubscriptionInvalidStateException.class)
    public ResponseEntity<ErrorResponse> handleSavingSubscriptionInvalidState(SavingExceptions.SavingSubscriptionInvalidStateException e) {
        log.warn("적금 상태 오류: {}", e.getMessage());
        return createErrorResponse(ErrorCode.SAVING_SUBSCRIPTION_INVALID_STATE);
    }

    @ExceptionHandler(SavingExceptions.SavingNoNextPlannedPaymentException.class)
    public ResponseEntity<ErrorResponse> handleSavingNoNextPlannedPayment() {
        return createErrorResponse(ErrorCode.SAVING_NO_NEXT_PLANNED_PAYMENT);
    }

    @ExceptionHandler(SavingExceptions.SavingInvalidPaymentAmountException.class)
    public ResponseEntity<ErrorResponse> handleSavingInvalidPaymentAmount() {
        return createErrorResponse(ErrorCode.SAVING_INVALID_PAYMENT_AMOUNT);
    }

    @ExceptionHandler(SavingExceptions.SavingInvalidDatesException.class)
    public ResponseEntity<ErrorResponse> handleSavingInvalidDates() {
        return createErrorResponse(ErrorCode.SAVING_INVALID_DATES);
    }

    @ExceptionHandler(SavingExceptions.SavingPaymentInvalidParamsException.class)
    public ResponseEntity<ErrorResponse> handleSavingPaymentInvalidParams(SavingExceptions.SavingPaymentInvalidParamsException e) {
        log.warn("적금 납입 파라미터 오류: {}", e.getMessage());
        return createErrorResponse(ErrorCode.SAVING_PAYMENT_INVALID_PARAMS);
    }

    @ExceptionHandler(SavingExceptions.SavingPolicyInvalidException.class)
    public ResponseEntity<ErrorResponse> handleSavingPolicyInvalid(SavingExceptions.SavingPolicyInvalidException e) {
        log.warn("적금 정책 파라미터 오류: {}", e.getMessage());
        return createErrorResponse(ErrorCode.SAVING_POLICY_INVALID);
    }

    @ExceptionHandler(SavingExceptions.SavingSnapshotIdentifiersInvalidException.class)
    public ResponseEntity<ErrorResponse> handleSavingSnapshotIdentifiersInvalid() {
        return createErrorResponse(ErrorCode.SAVING_SNAPSHOT_IDENTIFIERS_INVALID);
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

    @ExceptionHandler(CharacterAlreadyCreatedException.class)
    public ResponseEntity<ErrorResponse> handleCharacterAlreadyCreated(CharacterAlreadyCreatedException e) {
        return ResponseEntity
                .status(ErrorCode.CHARACTER_ALREADY_CREATED.getStatus())
                .body(ErrorResponse.of(ErrorCode.CHARACTER_ALREADY_CREATED));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("요청 파라미터 유효성 검증 실패: {}", e.getMessage());

        // ConstraintViolation -> ValidationFieldError
        java.util.List<ValidationFieldError> fieldErrors = new java.util.ArrayList<>();
        java.util.Set<ConstraintViolation<?>> violations = e.getConstraintViolations();

        for (ConstraintViolation<?> v : violations) {
            String field = extractLastPathNode(v.getPropertyPath()); // "getProducts.page" -> "page"
            Object rejected = v.getInvalidValue();
            String code = v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(); // Min, NotBlank 등
            String message = v.getMessage();

            fieldErrors.add(ValidationFieldError.of(field, rejected, code, message));
        }

        ValidationErrorResponse body = ValidationErrorResponse.of(ErrorCode.VALIDATION_ERROR, fieldErrors);
        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getStatus()).body(body);
    }

    /**
     * 경로에서 마지막 노드만 추출: "getProducts.page" -> "page"
     */
    private String extractLastPathNode(Path path) {
        String s = path.toString();
        int idx = s.lastIndexOf('.');
        if (idx >= 0 && idx + 1 < s.length()) {
            return s.substring(idx + 1);
        }
        return s;
    }
  
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception e, HttpServletRequest request) {
        log.error("예상하지 못한 예외 발생: {} {}", request.getMethod(), request.getRequestURI(), e);
        return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
