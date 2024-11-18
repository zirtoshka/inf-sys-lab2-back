package org.zir.dragonieze;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.zir.dragonieze.auth.JwtUtil;
import org.zir.dragonieze.dragon.EditableEntity;
import org.zir.dragonieze.user.Role;
import org.zir.dragonieze.user.User;
import org.zir.dragonieze.user.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import java.util.function.Function;

@Component
public class Controller {
    protected final JwtUtil jwtUtil;
    protected final UserRepository userRepository;
    protected final String HEADER_AUTH = "Authorization";

    @Autowired
    public Controller(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    String getUsername(String header, JwtUtil jwtUtil) {
        String jwt = header.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        return username;
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

    String getJson(Object entity) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(entity);
        return json;
    }


    @Transactional
    public <T> T saveEntityWithUser(String header, T entity, BiConsumer<T, User> setUserFunction, JpaRepository<T, ?> repository) {
        User user = getUserFromHeader(header);
        setUserFunction.accept(entity, user);
        return repository.save(entity);

    }


    @Transactional
    public <T extends EditableEntity> T updateEntityWithUser(String header,
                                                             T updatedEntity,
                                                             Long updatedEntityId,
                                                             Function<Long, Optional<T>> findByIdFunction,
                                                             Function<T, User> getOwnerFunction,
                                                             BiConsumer<T, T> updateFieldsFunction,
                                                             JpaRepository<T, ?> repository) {

        User user = getUserFromHeader(header);

        //check existing
        Optional<T> existingEntityOptional = findByIdFunction.apply(updatedEntityId);
        if (existingEntityOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        T existingEntity = existingEntityOptional.get();


        // check for update
        User owner = getOwnerFunction.apply(existingEntity);
        if ((user.getRole().equals(Role.ADMIN) && existingEntity.getCanEdit())
                || owner.getId().equals(user.getId())) {
            // updating
            updateFieldsFunction.accept(existingEntity, updatedEntity);

            // saving
            return repository.save(existingEntity);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this entity");
        }


    }


    private User getUserFromHeader(String header) {
        String username = getUsername(header, jwtUtil);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        String message = ex.getReason();
        return ResponseEntity.status(status).body(message);
    }


    public <T> T validateAndGetEntity(
            Long id, JpaRepository<T, Long> repository, String entityType
    ) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, entityType + " ID must not be null or invalid");
        }
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, entityType + " not found"));
    }
}
