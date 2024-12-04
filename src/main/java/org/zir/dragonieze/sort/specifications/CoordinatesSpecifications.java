package org.zir.dragonieze.sort.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.zir.dragonieze.dragon.Coordinates;

public class CoordinatesSpecifications extends GeneralSpecification<Coordinates> {
    public Specification<Coordinates> hasX(Double x) {
        return hasField("x", x);
    }

    public Specification<Coordinates> hasY(Float y) {
        return hasField("y", y);
    }


}
