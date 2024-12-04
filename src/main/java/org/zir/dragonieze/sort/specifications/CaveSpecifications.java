package org.zir.dragonieze.sort.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.zir.dragonieze.dragon.DragonCave;

@Component
public class CaveSpecifications extends GeneralSpecification<DragonCave> {

    public Specification<DragonCave> hasTreasures(Integer treasure) {
        return hasField("numberOfTreasures", treasure);
    }

}
