package org.zir.dragonieze.sort.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.zir.dragonieze.dragon.Coordinates;

public class CoordinatesSpecifications extends DataSpecifications {
    public static Specification<Coordinates> hasX(Double x) {
        return hasField("x", x);
    }

    public static Specification<Coordinates> hasY(Float y) {
        return hasField("y", y);
    }

    public static Specification<Coordinates> hasCanEdit(Boolean canEdit) {
        return hasField("canEdit", canEdit);
    }

    public static Specification<Coordinates> hasUserId(Long userId) {
        return hasField("user.id", userId);
    }

    public static Specification<Coordinates> hasId(Long id) {
        return hasField("id", id);
    }
}
