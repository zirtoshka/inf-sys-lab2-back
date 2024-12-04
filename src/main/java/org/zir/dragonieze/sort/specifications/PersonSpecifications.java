package org.zir.dragonieze.sort.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.zir.dragonieze.dragon.Color;
import org.zir.dragonieze.dragon.Country;
import org.zir.dragonieze.dragon.Person;

@Component
public class PersonSpecifications extends GeneralSpecification<Person> {

    public Specification<Person> hasName(String name) {
        return hasField("name", name);
    }

    public Specification<Person> hasHair(Color hairColor) {
        return hasField("hairColor", hairColor);
    }

    public Specification<Person> hasEyes(Color eyeColor) {
        return hasField("eyeColor", eyeColor);
    }

    public Specification<Person> hasLocation(Long locationId) {
        return hasField("location.id", locationId);
    }

    public Specification<Person> hasHeight(Integer height) {
        return hasField("height", height);
    }

    public Specification<Person> hasPassportID(String passportID) {
        return hasField("passportID", passportID);
    }

    public Specification<Person> hasNationality(Country nationality) {
        return hasField("nationality", nationality);
    }
}
