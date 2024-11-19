package org.zir.dragonieze.sort.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.zir.dragonieze.dragon.Color;
import org.zir.dragonieze.dragon.Country;
import org.zir.dragonieze.dragon.Person;

public class PersonSpecifications extends DataSpecifications {
    public static Specification<Person> hasCanEdit(boolean canEdit) {
        return hasField("canEdit", canEdit);
    }

    public static Specification<Person> hasUserId(Long userId) {
        return hasField("user.id", userId);
    }

    public static Specification<Person> hasId(Long id) {
        return hasField("id", id);
    }

    public static Specification<Person> hasName(String name) {
        return hasField("name", name);
    }

    public static Specification<Person> hasHair(Color hairColor) {
        return hasField("hairColor", hairColor);
    }

    public static Specification<Person> hasEyes(Color eyeColor) {
        return hasField("eyeColor", eyeColor);
    }

    public static Specification<Person> hasLocation(Long locationId) {
        return hasField("location.id", locationId);
    }

    public static Specification<Person> hasHeight(int height) {
        return hasField("height", height);
    }

    public static Specification<Person> hasPassportID(String passportID) {
        return hasField("passportID", passportID);
    }

    public static Specification<Person> hasNationality(Country nationality) {
        return hasField("nationality", nationality);
    }
}