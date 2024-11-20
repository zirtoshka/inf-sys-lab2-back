package org.zir.dragonieze.sort.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.zir.dragonieze.dragon.DragonCave;

public class CaveSpecifications extends DataSpecifications {
    public static Specification<DragonCave> hasUserId(Long userId) {
        return hasField("user.id", userId);
    }

    public static Specification<DragonCave> hasId(Long id) {
        return hasField("id", id);
    }

    public static Specification<DragonCave> hasCanEdit(boolean canEdit) {
        return hasField("canEdit", canEdit);
    }

    public static Specification<DragonCave> hasTreasures(Integer treasure) {
        return hasField("numberOfTreasures", treasure);
    }

}
