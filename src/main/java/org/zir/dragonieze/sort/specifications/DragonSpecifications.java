package org.zir.dragonieze.sort.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.zir.dragonieze.dragon.Color;
import org.zir.dragonieze.dragon.Dragon;
import org.zir.dragonieze.dragon.DragonCharacter;
import org.zir.dragonieze.dragon.Person;

import java.time.LocalDate;

public class DragonSpecifications extends DataSpecifications {
    public static Specification<Dragon> hasCanEdit(boolean canEdit) {
        return hasField("canEdit", canEdit);
    }

    public static Specification<Dragon> hasUserId(Long userId) {
        return hasField("user.id", userId);
    }

    public static Specification<Dragon> hasId(Long id) {
        return hasField("id", id);
    }

    public static Specification<Dragon> hasCoordinates(Long coordinatesId) {
        return hasField("coordinates.id", coordinatesId);
    }

    public static Specification<Dragon> hasName(String name) {
        return hasField("name", name);
    }

    public static Specification<Dragon> hasCreationDate(LocalDate creationDate) {
        return hasField("creationDate", creationDate);
    }

    public static Specification<Dragon> hasCave(Long caveId) {
        return hasField("cave.id", caveId);
    }

    public static Specification<Dragon> hasKiller(Long killerId) {
        return hasField("killer.id", killerId);
    }

    public static Specification<Dragon> hasAge(Integer age) {
        return hasField("age", age);
    }

    public static Specification<Dragon> hasWingspan(long wingspan) {
        return hasField("wingspan", wingspan);
    }

    public static Specification<Dragon> hasColor(Color color) {
        return hasField("color", color);
    }

    public static Specification<Dragon> hasCharacter(DragonCharacter character) {
        return hasField("character", character);
    }
}