package com.gftworkshop.cartMicroservice.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    @Mock
    private WebRequest webRequest;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHttpMessageConversionException() {
        String exceptionMessage = "Unrecognized token 'twenty-one': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')";
        HttpMessageConversionException exception = new HttpMessageConversionException(exceptionMessage);
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleHttpMessageConversionException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        String expectedMessage = "Invalid JSON format: " + exceptionMessage;
        assertEquals(expectedMessage, Objects.requireNonNull(responseEntity.getBody()).getMessage());
    }

    @Test
    void testHandleNumberFormatException() {
        NumberFormatException exception = new NumberFormatException("Invalid input");
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleNumberFormatException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid input", responseEntity.getBody().getMessage());
    }

    @Test
    void testHandleCartNotFoundException() {
        CartNotFoundException exception = new CartNotFoundException("Cart not found");
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleCartNotFoundException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Cart not found", responseEntity.getBody().getMessage());
    }

    @Test
    void testCartNotSuchElementException() {
        NoSuchElementException exception = new NoSuchElementException("No valid element");
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleCartNotSuchElement(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("No valid element", responseEntity.getBody().getMessage());
    }

    @Test
    void testCartProductInvalidQuantityException() {
        CartProductInvalidQuantityException exception = new CartProductInvalidQuantityException("Invalid quantity");
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleCartProductInvalidQuantityException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Invalid quantity", responseEntity.getBody().getMessage());
    }

    @Test
    void testHandleMethodArgumentNotValid() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("objectName", "fieldName", "Error message 1"));
        fieldErrors.add(new FieldError("objectName", "fieldName", "Error message 2"));

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleMethodArgumentNotValid(exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Validation failed: Error message 1; Error message 2; ", responseEntity.getBody().getMessage());
    }

    @Test
    void testHandleInternalErrorException() {
        InternalError exception = new InternalError("Internal server error");
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleGeneralException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Internal server error", responseEntity.getBody().getMessage());
    }
}
