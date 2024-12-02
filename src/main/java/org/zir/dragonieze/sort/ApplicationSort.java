package org.zir.dragonieze.sort;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum ApplicationSort {
    ID_ASC(Sort.by(Sort.Direction.ASC, "id")),
    ID_DESC(Sort.by(Sort.Direction.DESC, "id")),

    DATE_ASC(Sort.by(Sort.Direction.ASC, "createdAt")),
    DATE_DESC(Sort.by(Sort.Direction.DESC, "createdAt")),

    USER_ASC(Sort.by(Sort.Direction.ASC, "userId")),
    USER_DESC(Sort.by(Sort.Direction.DESC, "userId"));

    private final Sort sortValue;
}
