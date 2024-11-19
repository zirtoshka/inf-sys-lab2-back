package org.zir.dragonieze.sort.specifications;


import org.springframework.data.jpa.domain.Specification;
import org.zir.dragonieze.dragon.DragonHead;

public class HeadSpecifications extends DataSpecifications {

    public static Specification<DragonHead> hasUserId(Long userId) {
        return hasField("user.id", userId);
    }

    public static Specification<DragonHead> hasId(Long id) {
        return hasField("id", id);
    }

    public static Specification<DragonHead> hasCanEdit(boolean canEdit) {
        return hasField("canEdit", canEdit);
    }

    public static Specification<DragonHead> hasEyes(double eyesCount) {
        return hasField("eyesCount", eyesCount);
    }
}
