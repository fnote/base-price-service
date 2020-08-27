package com.sysco.rps.controller;

import com.sysco.rps.common.Errors;
import com.sysco.rps.dto.ErrorDTO;
import com.sysco.rps.exceptions.RefPriceAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.UUID;

import static java.text.MessageFormat.format;

/**
 * Class based global exception handlers
 *
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 13. Jul 2020 08:21
 */
public abstract class AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);
    private static final String ERROR_PLACEHOLDER = "errors.{0}";
    private static final String UNKNOWN_ERROR = "Unknown Error";

    @Autowired
    private MessageSource messages;

    @ExceptionHandler(RefPriceAPIException.class)
    ResponseEntity<Mono<ErrorDTO>> handleRefPriceApiException(RefPriceAPIException e) {
        String traceId = UUID.randomUUID().toString();

        LOGGER.error("[{}] RefPriceAPIException occurred", traceId, e);

        String message = this.messages.getMessage(format(ERROR_PLACEHOLDER, (e.getErrorCode())), new Object[]{},
              UNKNOWN_ERROR, Locale.getDefault());

        ErrorDTO error;
        if (e.getAdditionalInfo() != null) {
            error = new ErrorDTO(e.getErrorCode(), message, e.getAdditionalInfo(), traceId);
        } else {
            error = new ErrorDTO(e.getErrorCode(), message, e.getMessage(), traceId);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(Mono.just(error), headers, e.getHttpStatusCode());
    }

    /**
     * Handle Exceptions associated with specific HTTP response status codes
     *
     * @param e ResponseStatusException
     * @return
     */
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Mono<ErrorDTO>> handleResponseStatusException(ResponseStatusException e) {
        String traceId = UUID.randomUUID().toString();
        LOGGER.error("[{}] ResponseStatusException occurred", traceId, e);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ErrorDTO error = new ErrorDTO(Errors.Codes.UNEXPECTED_ERROR, e.getStatus().getReasonPhrase(), traceId);
        return new ResponseEntity<>(Mono.just(error), headers, e.getStatus());
    }


    /**
     * Handle Unknown Exceptions
     *
     * @param e Exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    ResponseEntity<Mono<ErrorDTO>> handleUnknownException(Exception e) {
        String traceId = UUID.randomUUID().toString();
        LOGGER.error("[{}] Unknown exception occurred", traceId, e);

        String message = this.messages.getMessage(format(ERROR_PLACEHOLDER, Errors.Codes.UNEXPECTED_ERROR), new Object[]{}, UNKNOWN_ERROR,
              Locale.getDefault());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ErrorDTO error = new ErrorDTO(Errors.Codes.UNEXPECTED_ERROR, message, traceId);
        return new ResponseEntity<>(Mono.just(error), headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
