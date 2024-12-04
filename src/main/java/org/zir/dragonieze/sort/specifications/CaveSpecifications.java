package org.zir.dragonieze.sort.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.zir.dragonieze.dragon.DragonCave;

public class CaveSpecifications extends GeneralSpecification<DragonCave> {

    public static Specification<DragonCave> hasTreasures(Integer treasure) {
        return hasField("numberOfTreasures", treasure);
    }

}
