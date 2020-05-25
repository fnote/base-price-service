package com.sysco.rps.controller;

import static com.sysco.rps.service.exception.ErrorCode.ACCESS_DENIED;
import static com.sysco.rps.service.exception.ErrorCode.BAD_REQUEST;
import static com.sysco.rps.service.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.sysco.rps.service.exception.ErrorCode.NO_HANDLER_FOUND;
import static com.sysco.rps.service.exception.ErrorCode.RECORD_LOCKED;
import static com.sysco.rps.service.exception.ErrorCode.RECORD_NOT_FOUND;
import static com.sysco.rps.service.exception.ErrorCode.VALIDATION_FAILURE;

import com.sysco.rps.dto.ErrorDTO;
import com.sysco.rps.service.exception.DuplicateRecordException;
import com.sysco.rps.service.exception.ErrorCode;
import com.sysco.rps.service.exception.RecordLockedException;
import com.sysco.rps.service.exception.RecordNotFoundException;
import com.sysco.rps.service.exception.ValidationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.beans.TypeMismatchException;
import org.springframework.security.access.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;


/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/13/20 Time: 12:54 PM
 */
@ControllerAdvice
@RestController
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handles RecordNotFoundException
   * <p>This method delegates to {@link #handleExceptionInternal}
   *
   * @param ex      the exception
   * @param request the current request
   * @return a {@code ResponseEntity} instance with HttpStatus of Not Found - 404
   */
  @ExceptionHandler(value = {RecordNotFoundException.class})
  protected ResponseEntity<Object> handleNotFound(RecordNotFoundException ex, WebRequest request) {
    return handleExceptionWithoutResponseBody(ex, request, HttpStatus.NOT_FOUND, RECORD_NOT_FOUND);
  }

  /**
   * Handles RecordLockedException
   * <p>This method delegates to {@link #handleExceptionInternal}
   *
   * @param ex      the exception
   * @param request the current request
   * @return a {@code ResponseEntity} instance with HttpStatus of Conflict - 409
   */
  @ExceptionHandler(value = {RecordLockedException.class})
  protected ResponseEntity<Object> handleRecordLocked(RecordLockedException ex, WebRequest request) {
    return handleExceptionWithoutResponseBody(ex, request, HttpStatus.CONFLICT, RECORD_LOCKED);
  }

  /**
   * Handles AccessDeniedException
   * <p>This method delegates to {@link #handleExceptionInternal}
   *
   * @param ex      the exception
   * @param request the current request
   * @return a {@code ResponseEntity} instance with HttpStatus of Forbidden - 403
   */
  @ExceptionHandler(value = {AccessDeniedException.class})
  protected ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
    return handleExceptionWithoutResponseBody(ex, request, HttpStatus.FORBIDDEN, ACCESS_DENIED);
  }

  /**
   * Handles AuthenticationException
   * <p>This method delegates to {@link #handleExceptionInternal}
   *
   * @param ex      the exception
   * @param request the current request
   * @return a {@code ResponseEntity} instance with HttpStatus of Unauthorized - 401
   */
  @ExceptionHandler(value = {BadCredentialsException.class})
  protected ResponseEntity<Object> handleUnAuthenticated(BadCredentialsException ex, WebRequest request) {
    return handleExceptionWithoutResponseBody(ex, request, HttpStatus.UNAUTHORIZED, ACCESS_DENIED);
  }

  /**
   * Handles Exception - to cover all the other exception types
   * <p>This method delegates to {@link #handleExceptionInternal}
   *
   * @param ex      the exception
   * @param request the current request
   * @return a {@code ResponseEntity} instance with HttpStatus of Internal Server Error - 500
   */
  @ExceptionHandler(value = {Exception.class})
  protected ResponseEntity<Object> handleUnhandled(Exception ex, WebRequest request) {
    return handleExceptionWithoutResponseBody(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
  }

  /**
   * Handles ConstraintViolationException
   * <p>This exception is thrown when a request parameter or path variable violates its constraints</p>
   * <p>This method delegates to {@link #handleExceptionInternal}
   *
   * @param ex      the exception
   * @param request the current request
   * @return a {@code ResponseEntity} instance with HttpStatus of Bad Request - 400
   */
  @ExceptionHandler(value = {ConstraintViolationException.class})
  protected ResponseEntity<Object> handleInvalidParams(ConstraintViolationException ex, WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getConstraintViolations().forEach(error -> {
      String fieldName = ((PathImpl) error.getPropertyPath()).getLeafNode().toString();
      String errorMessage = error.getMessage();
      errors.put(fieldName, errorMessage);

    });
    ErrorDTO errorDTO = new ErrorDTO(VALIDATION_FAILURE.getCode(), VALIDATION_FAILURE.getDescription(), errors);
    return handleExceptionInternal(ex, errorDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Handles ValidationException
   * <p>This exception is thrown when the service level constraints are violated</p>
   * <p>This method delegates to {@link #handleExceptionInternal}
   *
   * @param ex      the exception
   * @param request the current request
   * @return a {@code ResponseEntity} instance with HttpStatus of Bad Request - 400
   */
  @ExceptionHandler(value = {ValidationException.class})
  protected ResponseEntity<Object> handleValidation(ValidationException ex, WebRequest request) {
    log.error(ex.getMessage(), ex);
    return handleExceptionInternal(ex, ex.getErrorDTO(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Handles DuplicateRecordException
   * <p>This exception is thrown when a duplicate record is found upon insertion or update</p>
   * <p>This method delegates to {@link #handleExceptionInternal}
   *
   * @param ex      the exception
   * @param request the current request
   * @return a {@code ResponseEntity} instance with HttpStatus of Conflict - 409
   */
  @ExceptionHandler(value = {DuplicateRecordException.class})
  protected ResponseEntity<Object> handleDuplicateRecord(DuplicateRecordException ex, WebRequest request) {
    log.error(ex.getMessage(), ex);
    return handleExceptionInternal(ex, ex.getErrorDTO(), new HttpHeaders(), HttpStatus.CONFLICT, request);
  }

  /**
   * Override the super class method for TypeMismatchException.
   * <p>This exception is thrown when the controller level request parameter types are violated</p>
   * <p>This method delegates to {@link #handleExceptionInternal}.
   *
   * @param ex      the exception
   * @param headers the headers to be written to the response
   * @param status  the selected response status
   * @param request the current request
   * @return
   */
  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.error(ex.getMessage(), ex);
    return handleExceptionWithoutResponseBody(ex, request, HttpStatus.BAD_REQUEST, VALIDATION_FAILURE);
  }

  /**
   * Override the super class method for ServletRequestBindingException.
   * <p>This exception is thrown when the controller level request parameter are violated</p>
   * <p>This method delegates to {@link #handleExceptionInternal}.
   *
   * @param ex      the exception
   * @param headers the headers to be written to the response
   * @param status  the selected response status
   * @param request the current request
   * @return
   */
  @Override
  protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    log.error(ex.getMessage(), ex);
    return handleExceptionWithoutResponseBody(ex, request, HttpStatus.BAD_REQUEST, VALIDATION_FAILURE);
  }

  /**
   * Override the super class method for MethodArgumentNotValidException.
   * <p>This exception is thrown when the controller level validations are violated</p>
   * <p>This method delegates to {@link #handleExceptionInternal}.
   *
   * @param ex      the exception
   * @param headers the headers to be written to the response
   * @param status  the selected response status
   * @param request the current request
   * @return a {@code ResponseEntity} instance with HttpStatus of Bad Request - 400
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);

    });
    ErrorDTO errorDTO = new ErrorDTO(VALIDATION_FAILURE.getCode(), VALIDATION_FAILURE.getDescription(), errors);
    return handleExceptionInternal(ex, errorDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Override the super class method for HttpMessageNotReadableException
   * <p>This exception is thrown when an incorrect data type is provided</p>
   * <p>This method delegates to {@link #handleExceptionInternal}.
   *
   * @param ex      the exception
   * @param headers the headers to be written to the response
   * @param status  the selected response status
   * @param request the current request
   * @return a {@code ResponseEntity} instance with HttpStatus of Bad Request - 400
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.error(ex.getMessage(), ex);
    return handleExceptionWithoutResponseBody(ex, request, HttpStatus.BAD_REQUEST, BAD_REQUEST);
  }

  /**
   * Override the super class method for NoHandlerFoundException
   * <p>This exception is thrown when attempting to access incorrect path</p>
   * <p>This method delegates to {@link #handleExceptionInternal}
   *
   * @param ex      the exception
   * @param headers the headers to be written to the response
   * @param status  the selected response status
   * @param request the current request
   * @return a {@code ResponseEntity} instance with HttpStatus of Not Found - 404
   */
  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    log.error(ex.getMessage(), ex);
    return handleExceptionWithoutResponseBody(ex, request, HttpStatus.NOT_FOUND, NO_HANDLER_FOUND);
  }


  private ResponseEntity<Object> handleExceptionWithoutResponseBody(Exception ex, WebRequest request, HttpStatus httpStatus, ErrorCode errorCode) {
    ErrorDTO errorDTO = new ErrorDTO(errorCode.getCode(), errorCode.getDescription());
    log.error(ex.getMessage(), ex);
    return handleExceptionInternal(ex, errorDTO, new HttpHeaders(), httpStatus, request);
  }
}

