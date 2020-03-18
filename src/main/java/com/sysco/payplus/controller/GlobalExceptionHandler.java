package com.sysco.payplus.controller;
import com.sysco.payplus.dto.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/13/20
 * Time: 12:54 PM
 */
@ControllerAdvice
@RestController
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger("Sysco.GlobalExceptionHandler");

    private static final String ERROR_LOG_FORMAT = "incident Id : {}, exception : {}";

    @ExceptionHandler(value
            = { Exception.class})
    protected ResponseEntity<Object> handleUnhandled(
            Exception ex, WebRequest request) {
        ErrorDTO errorDTO=new ErrorDTO(ErrorDTO.ERROR_CODE_INTERNAL_ERROR);
        return handleExceptionInternal(ex, errorDTO,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


    /**
     * Override the super class method for MethodArgumentNotValidException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     * @param ex the exception
     * @param headers the headers to be written to the response
     * @param status the selected response status
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     */
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorDTO errorDTO=new ErrorDTO(ErrorDTO.ERROR_CODE_VALIDATION_FAILURE,errors);
        return handleExceptionInternal(ex, errorDTO,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);

    }



    /*@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;

    }*/
}

