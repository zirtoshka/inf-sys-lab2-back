package org.zir.dragonieze.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.zir.dragonieze.dragon.GeneralEntity;
import org.zir.dragonieze.openam.api.OpenAmRestApiClient;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;
import org.zir.dragonieze.user.Role;
import org.zir.dragonieze.user.User;
import org.zir.dragonieze.user.UserRepository;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
public class BaseService {
    protected final UserRepository userRepository;

    @Autowired
    public BaseService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public <T extends GeneralEntity> T updateEntityWithUser(OpenAmUserPrincipal principal,
                                                            T updatedEntity,
                                                            Long updatedEntityId,
                                                            Function<Long, Optional<T>> findByIdFunction,
                                                            Function<T, User> getOwnerFunction,
                                                            BiConsumer<T, T> updateFieldsFunction,
                                                            JpaRepository<T, ?> repository) {

        User user = principal.getUser();

        //check existing
        Optional<T> existingEntityOptional = findByIdFunction.apply(updatedEntityId);
        if (existingEntityOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        T existingEntity = existingEntityOptional.get();


        // check for update
        User owner = getOwnerFunction.apply(existingEntity);
        if ((principal.hasRole(Role.ADMIN) && existingEntity.getCanEdit()) || owner.getId().equals(user.getId())) {
            // updating
            System.out.println("Updating entity");
            updateFieldsFunction.accept(existingEntity, updatedEntity);

            // saving
            return repository.save(existingEntity);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this entity");
        }
    }


    @Transactional
    public <T extends GeneralEntity> void deleteEntityWithCondition(
            OpenAmUserPrincipal principal,
            Long entityId,
            Function<T, User> getOwnerFunction,
            JpaRepository<T, Long> repository
    ) {
        User user = principal.getUser();

        T entity = repository.findById(entityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found"));

        User owner = getOwnerFunction.apply(entity);

        if ((principal.hasRole(Role.ADMIN) && entity.getCanEdit())
                || owner.getId().equals(user.getId())) {
            repository.delete(entity);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this entity");
        }
    }



    public <T> T saveEntityWithUser(OpenAmUserPrincipal user, T entity, BiConsumer<T, User> setUserFunction, JpaRepository<T, ?> repository) {
        setUserFunction.accept(entity, user.getUser());
        return repository.save(entity);
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

    public String convertToJson(Object entity) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(entity);
        return json;
    }


}
