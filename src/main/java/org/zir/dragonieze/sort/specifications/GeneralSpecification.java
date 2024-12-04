package org.zir.dragonieze.sort.specifications;


import org.springframework.data.jpa.domain.Specification;
import org.zir.dragonieze.dragon.GeneralEntity;

public class GeneralSpecification<T extends GeneralEntity> extends DataSpecifications{
    public Specification<T> hasCanEdit(boolean canEdit) {
        return hasField("canEdit", canEdit);
    }

    public  Specification<T> hasUserId(Long userId) {
        return hasField("user.id", userId);
    }

    public  Specification<T> hasId(Long id) {
        return hasField("id", id);
    }
}
