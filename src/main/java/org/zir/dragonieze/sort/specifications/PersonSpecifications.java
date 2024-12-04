package org.zir.dragonieze.sort.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.zir.dragonieze.dragon.Color;
import org.zir.dragonieze.dragon.Country;
import org.zir.dragonieze.dragon.Person;

@Component
public class PersonSpecifications extends GeneralSpecification<Person> {

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

    public static Specification<Person> hasHeight(Integer height) {
        return hasField("height", height);
    }

    public static Specification<Person> hasPassportID(String passportID) {
        return hasField("passportID", passportID);
    }

    public static Specification<Person> hasNationality(Country nationality) {
        return hasField("nationality", nationality);
    }
}
