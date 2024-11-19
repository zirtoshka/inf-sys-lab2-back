package org.zir.dragonieze.sort.specifications;


import org.springframework.data.jpa.domain.Specification;
import org.zir.dragonieze.dragon.Location;

public class LocationSpecifications extends DataSpecifications{
    public static Specification<Location> hasX(float x) {
        return hasField("x", x);
    }

    public static Specification<Location> hasY(Integer y) {
        return hasField("y", y);
    }
    public static Specification<Location> hasZ(Float z) {
        return hasField("z", z);
    }

    public static Specification<Location> hasCanEdit(boolean canEdit) {
        return hasField("canEdit", canEdit);
    }

    public static Specification<Location> hasUserId(Long userId) {
        return hasField("user.id", userId);
    }

    public static Specification<Location> hasName(String name) {
        return hasField("name", name);
    }

    public static Specification<Location> hasId(Long id) {
        return hasField("id", id);
    }
}
