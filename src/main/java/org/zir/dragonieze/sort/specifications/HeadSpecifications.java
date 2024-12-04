package org.zir.dragonieze.sort.specifications;


import org.springframework.data.jpa.domain.Specification;
import org.zir.dragonieze.dragon.DragonHead;

public class HeadSpecifications extends GeneralSpecification<DragonHead> {

    public static Specification<DragonHead> hasEyes(double eyesCount) {
        return hasField("eyesCount", eyesCount);
    }
}
