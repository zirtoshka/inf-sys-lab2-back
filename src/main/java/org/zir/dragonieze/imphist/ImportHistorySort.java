package org.zir.dragonieze.imphist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum ImportHistorySort {
    ID_ASC(Sort.by(Sort.Direction.ASC, "id")),
    ID_DESC(Sort.by(Sort.Direction.DESC, "id")),

    USER_ASC(Sort.by(Sort.Direction.ASC, "userId")),
    USER_DESC(Sort.by(Sort.Direction.DESC, "userId"));
    private final Sort sortValue;

}
