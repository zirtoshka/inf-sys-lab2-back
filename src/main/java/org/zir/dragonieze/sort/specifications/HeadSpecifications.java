package org.zir.dragonieze.sort.specifications;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.zir.dragonieze.dragon.DragonHead;

@Component
public class HeadSpecifications extends GeneralSpecification<DragonHead> {

    public static Specification<DragonHead> hasEyes(Double eyesCount) {
        return hasField("eyesCount", eyesCount);
    }
}
