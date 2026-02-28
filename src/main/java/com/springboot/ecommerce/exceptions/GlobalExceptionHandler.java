package com.springboot.ecommerce.exceptions;

import com.springboot.ecommerce.dtos.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationErrors(
			MethodArgumentNotValidException ex) {

		List<String> errors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(e -> e.getField() + ": " + e.getDefaultMessage())
				.collect(Collectors.toList());

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ApiResponse<>(false, errors, null));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Object> handleRuntimeException(
			RuntimeException ex) {

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ApiResponse<>(false, List.of(ex.getMessage()), null));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUnexpectedException(
			Exception ex) {

		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiResponse<>(
						false,
						List.of("Internal server error"),
						null
				));
	}


	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
				false,
				List.of(ex.getMessage()),
				null));
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<?> handleDuplicateResourceException(DuplicateResourceException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(
				false,
				List.of(ex.getMessage()),
				null));
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(
				false,
				List.of(ex.getMessage()),
				null));
	}

}