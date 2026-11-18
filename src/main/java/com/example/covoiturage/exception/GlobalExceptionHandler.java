package com.example.covoiturage.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex,
                                                        HttpServletRequest request) {
        log.warn("Ressource introuvable : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(problemDetail(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), request));
    }


    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ProblemDetail> handleForbidden(ForbiddenException ex,
                                                         HttpServletRequest request) {
        log.warn("Action refusée : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(problemDetail(HttpStatus.FORBIDDEN, "FORBIDDEN", ex.getMessage(), request));
    }


    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflict(ConflictException ex,
                                                        HttpServletRequest request) {
        log.warn("Règle métier violée : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(problemDetail(HttpStatus.CONFLICT, "BUSINESS_RULE_VIOLATION", ex.getMessage(), request));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException ex,
                                                             HttpServletRequest request) {
        log.warn("Contrainte d'intégrité violée : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(problemDetail(HttpStatus.CONFLICT, "DATA_INTEGRITY_VIOLATION",
                        "Donnée dupliquée ou invalide (unicité / contrainte de base de données)", request));
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(BadRequestException ex,
                                                          HttpServletRequest request) {
        log.warn("Requête invalide : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(problemDetail(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() == null ? "valeur invalide" : error.getDefaultMessage(),
                        (first, second) -> first,
                        TreeMap::new
                ));

        log.warn("Validation échouée : {}", fieldErrors);
        ProblemDetail problem = problemDetail(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "La requête contient des champs invalides", request);
        problem.setProperty("fieldErrors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest request) {
        Map<String, String> violations = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (first, second) -> first,
                        TreeMap::new
                ));

        log.warn("Violation de contraintes : {}", violations);
        ProblemDetail problem = problemDetail(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Les paramètres de la requête sont invalides", request);
        problem.setProperty("fieldErrors", violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ProblemDetail> handleMalformedRequest(Exception ex,
                                                                HttpServletRequest request) {
        log.warn("Requête mal formée : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(problemDetail(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST",
                        "JSON invalide ou paramètre manquant/incorrect", request));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex,
                                                          HttpServletRequest request) {
        log.error("Erreur interne inattendue", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(problemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                        "Une erreur interne est survenue", request));
    }


    private ProblemDetail problemDetail(HttpStatus status,
                                        String errorCode,
                                        String message,
                                        HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, message);
        problem.setTitle(status.getReasonPhrase());
        problem.setType(URI.create("urn:covoiturage:error:"
                + errorCode.toLowerCase(Locale.ROOT).replace('_', '-')));
        problem.setInstance(URI.create(request.getRequestURI()));

        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("code", status.value());
        problem.setProperty("error", errorCode);
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }
}
