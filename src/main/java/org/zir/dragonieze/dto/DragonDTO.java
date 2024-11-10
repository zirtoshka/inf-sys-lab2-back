package org.zir.dragonieze.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.zir.dragonieze.dragon.Color;

import org.zir.dragonieze.dragon.DragonCharacter;

@Getter
@Setter
@AllArgsConstructor
public class DragonDTO {
    private long id;
    private String name;
    private CoordinatesDTO coordinates;
    private java.time.LocalDate creationDate;
    private DragonCaveDTO cave;
    private PersonDTO killer;
    private Integer age;
    private long wingspan;
    private Color color;
    private DragonCharacter character;
    private boolean canEdit;

}
