package org.zir.dragonieze.sort.specifications;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.zir.dragonieze.dragon.Location;

@Component
public class LocationSpecifications extends GeneralSpecification<Location> {
    public static Specification<Location> hasX(float x) {
        return hasField("x", x);
    }

    public static Specification<Location> hasY(Integer y) {
        return hasField("y", y);
    }

    public static Specification<Location> hasZ(Float z) {
        return hasField("z", z);
    }

    public static Specification<Location> hasName(String name) {
        return hasField("name", name);
    }

}
