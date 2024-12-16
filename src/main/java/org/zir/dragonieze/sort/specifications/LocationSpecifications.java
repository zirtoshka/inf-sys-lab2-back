package org.zir.dragonieze.sort.specifications;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.zir.dragonieze.dragon.Location;

@Component
public class LocationSpecifications extends GeneralSpecification<Location> {
    public Specification<Location> hasX(Float x) {
        return hasField("x", x);
    }

    public Specification<Location> hasY(Integer y) {
        return hasField("y", y);
    }

    public Specification<Location> hasZ(Float z) {
        return hasField("z", z);
    }

    public Specification<Location> hasName(String name) {
        return hasFieldLike("name", name);
    }


}
