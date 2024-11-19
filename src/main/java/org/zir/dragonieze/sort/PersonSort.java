package org.zir.dragonieze.sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum PersonSort {
    ID_ASC(Sort.by(Sort.Direction.ASC, "id")),
    ID_DESC(Sort.by(Sort.Direction.DESC, "id")),

    NAME_ASC(Sort.by(Sort.Direction.ASC, "name")),
    NAME_DESC(Sort.by(Sort.Direction.DESC, "name")),

    EYE_ASC(Sort.by(Sort.Direction.ASC, "eyeColor")),
    EYE_DESC(Sort.by(Sort.Direction.DESC, "eyeColor")),

    HAIR_ASC(Sort.by(Sort.Direction.ASC, "hairColor")),
    HAIR_DESC(Sort.by(Sort.Direction.DESC, "hairColor")),

    LOCATION_ASC(Sort.by(Sort.Direction.ASC, "location.id")),
    LOCATION_DESC(Sort.by(Sort.Direction.DESC, "location.id")),//todo

    HEIGHT_ASC(Sort.by(Sort.Direction.ASC, "height")),
    HEIGHT_DESC(Sort.by(Sort.Direction.DESC, "height")),

    PASSPORT_ASC(Sort.by(Sort.Direction.ASC, "passportID")),
    PASSPORT_DESC(Sort.by(Sort.Direction.DESC, "passportID")),

    NATIONALITY_SORT_ASC(Sort.by(Sort.Direction.ASC, "nationality")),
    NATIONALITY_SORT_DESC(Sort.by(Sort.Direction.DESC, "nationality"));

    private final Sort sortValue;
}
