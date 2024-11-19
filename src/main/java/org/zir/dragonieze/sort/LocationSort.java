package org.zir.dragonieze.sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum LocationSort {
    ID_ASC(Sort.by(Sort.Direction.ASC, "id")),
    ID_DESC(Sort.by(Sort.Direction.DESC, "id")),

    X_ASC(Sort.by(Sort.Direction.ASC, "x")),
    X_DESC(Sort.by(Sort.Direction.DESC, "x")),

    Y_ASC(Sort.by(Sort.Direction.ASC, "y")),
    Y_DESC(Sort.by(Sort.Direction.DESC, "y")),

    Z_ASC(Sort.by(Sort.Direction.ASC, "z")),
    Z_DESC(Sort.by(Sort.Direction.DESC, "z")),

    NAME_ASC(Sort.by(Sort.Direction.ASC, "name")),
    NAME_DESC(Sort.by(Sort.Direction.DESC, "name"));

    private final Sort sortValue;
}
