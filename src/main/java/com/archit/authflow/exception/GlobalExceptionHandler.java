
package com.archit.authflow.exception;

import com.archit.authflow.dto.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<MessageResponse> handleUserExists(
            UserAlreadyExistsException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<MessageResponse> handleInvalidOtp(
            InvalidOtpException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(AlreadyVerifiedException.class)
    public ResponseEntity<MessageResponse> handleAlreadyVerified(
            AlreadyVerifiedException ex) {

        return ResponseEntity
                .badRequest()
                .body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(TooManyResendAttemptsException.class)
    public ResponseEntity<MessageResponse> handleTooManyResends(
            TooManyResendAttemptsException ex) {

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<MessageResponse> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<MessageResponse> handleEmailNotVerified(
            EmailNotVerifiedException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new MessageResponse(ex.getMessage()));
    }
}