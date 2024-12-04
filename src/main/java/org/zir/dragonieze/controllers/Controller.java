package org.zir.dragonieze.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.zir.dragonieze.dragon.GeneralEntity;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.sort.specifications.GeneralSpecification;

import java.util.HashMap;
import java.util.Map;

@Component
public class Controller {

    protected final String HEADER_AUTH = "Authorization";
    protected final BaseService service;
    protected final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public Controller(BaseService service, SimpMessagingTemplate messagingTemplate) {
        this.service = service;
        this.messagingTemplate = messagingTemplate;
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        String message = ex.getReason();
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }


    public <T extends GeneralEntity> Specification<T> canEditSpec(Boolean canEdit,
                                                              Specification<T> spec,
                                                              GeneralSpecification<T> genspec) {
        if (canEdit != null) {
            return spec.and(genspec.hasCanEdit(canEdit));
        }
        return spec;
    }


}
