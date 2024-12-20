package org.zir.dragonieze.sort.specifications;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.zir.dragonieze.dragon.Color;
import org.zir.dragonieze.dragon.Coordinates;
import org.zir.dragonieze.dragon.Dragon;
import org.zir.dragonieze.dragon.DragonCharacter;

import java.time.LocalDate;

@Component
public class DragonSpecifications extends GeneralSpecification<Dragon> {

    public Specification<Dragon> hasCoordinates(Long coordinatesId) {
        return hasField("coordinates.id", coordinatesId);
    }

    public Specification<Dragon> hasName(String name) {
        return hasField("name", name);
    }

    public Specification<Dragon> hasCreationDate(LocalDate creationDate) {
        return hasField("creationDate", creationDate);
    }

    public Specification<Dragon> hasCave(Long caveId) {
        return hasField("cave.id", caveId);
    }

    public Specification<Dragon> hasKiller(Long killerId) {
        return hasField("killer.id", killerId);
    }

    public Specification<Dragon> hasAge(Integer age) {
        return hasField("age", age);
    }

    public Specification<Dragon> hasWingspan(Long wingspan) {
        return hasField("wingspan", wingspan);
    }

    public Specification<Dragon> hasColor(Color color) {
        return hasField("color", color);
    }

    public Specification<Dragon> hasCharacter(DragonCharacter character) {
        return hasField("character", character);
    }

    public Specification<Dragon> hasHeads(Integer countHeads) {
        return hasField("heads.len", countHeads);
    }

    public static Specification<Dragon> coordinatesInRectangle(Double minX, Double maxX, Float minY, Float maxY) {
        return (root, query, criteriaBuilder) -> {
            Join<Dragon, Coordinates> coordinatesJoin = root.join("coordinates");
            return criteriaBuilder.and(
                    criteriaBuilder.between(coordinatesJoin.get("x"), minX, maxX),
                    criteriaBuilder.between(coordinatesJoin.get("y"), minY, maxY)
            );
        };
    }
}