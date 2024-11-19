package org.zir.dragonieze.sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum DragonSort {
    ID_ASC(Sort.by(Sort.Direction.ASC, "id")),
    ID_DESC(Sort.by(Sort.Direction.DESC, "id")),

    NAME_ASC(Sort.by(Sort.Direction.ASC, "name")),
    NAME_DESC(Sort.by(Sort.Direction.DESC, "name")),

    COORD_ASC(Sort.by(Sort.Direction.ASC, "coordinates.id")),//todo
    COORD_DESC(Sort.by(Sort.Direction.DESC, "coordinates.id")),

    DATE_ASC(Sort.by(Sort.Direction.ASC, "creationDate")),
    DATE_DESC(Sort.by(Sort.Direction.DESC, "creationDate")),

    CAVE_ASC(Sort.by(Sort.Direction.ASC, "cave.id")),//todo
    CAVE_DESC(Sort.by(Sort.Direction.DESC, "cave.id")),

    PERSON_ASC(Sort.by(Sort.Direction.ASC, "person.id")), //todo
    PERSON_DESC(Sort.by(Sort.Direction.DESC, "person.id")),

    AGE_ASC(Sort.by(Sort.Direction.ASC, "age")),
    AGE_DESC(Sort.by(Sort.Direction.DESC, "age")),

    WINGSPAN_ASC(Sort.by(Sort.Direction.ASC, "wingspan")),
    WINGSPAN_DESC(Sort.by(Sort.Direction.DESC, "wingspan")),

    COLOR_ASC(Sort.by(Sort.Direction.ASC, "color")),
    COLOR_DESC(Sort.by(Sort.Direction.DESC, "color")),

    CHARACTER_ASC(Sort.by(Sort.Direction.ASC, "character")),
    CHARACTER_DESC(Sort.by(Sort.Direction.DESC, "character")),


    HEADS_ASC(Sort.by(Sort.Direction.ASC, "heads.size")),
    HEADS_DESC(Sort.by(Sort.Direction.DESC, "heads.size"));//todo



    private final Sort sortValue;
}
