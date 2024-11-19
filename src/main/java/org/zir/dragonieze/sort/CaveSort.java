package org.zir.dragonieze.sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum CaveSort {
    ID_ASC(Sort.by(Sort.Direction.ASC, "id")),
    ID_DESC(Sort.by(Sort.Direction.DESC, "id")),

    TREASURE_ASC(Sort.by(Sort.Direction.ASC, "numberOfTreasures")),
    TREASURE_DESC(Sort.by(Sort.Direction.DESC, "numberOfTreasures"));

    private final Sort sortValue;

}
