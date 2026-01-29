package com.azhar.urlshortener.controller;

import com.azhar.urlshortener.dto.ErrorResponse;
import com.azhar.urlshortener.dto.UrlRequest;
import com.azhar.urlshortener.dto.UrlResponse;
import com.azhar.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;

@Tag(name = "URL Shortener", description = "APIs for URL shortening and redirection")
@RestController
@RequestMapping("/api/v1/url")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody UrlRequest urlRequest) {
        UrlResponse response = urlService.createShortUrl(urlRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Redirect to original URL")

    @GetMapping("/{shortCode}")
    public void redirectToOriginalUrl(@PathVariable String shortCode,
            HttpServletResponse response) throws IOException {
        try {
            UrlResponse urlResponse = urlService.getOriginalUrl(shortCode);
            response.sendRedirect(urlResponse.getOriginalUrl());
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.GONE) {
                response.sendError(HttpStatus.GONE.value(), "URL has expired");
            } else {
                response.sendError(HttpStatus.NOT_FOUND.value(), "URL not found");
            }
        }
    }

    @Operation(summary = "Get URL statistics")
    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlResponse> getUrlStats(@PathVariable String shortCode) {
        UrlResponse response = urlService.getUrlStats(shortCode);
        return ResponseEntity.ok(response);
    }

    // Exception handlers
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex,
            jakarta.servlet.http.HttpServletRequest request) {

        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getStatusCode().value(),
                status != null ? status.getReasonPhrase() : "Error",
                ex.getReason(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex,
            jakarta.servlet.http.HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred",
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
