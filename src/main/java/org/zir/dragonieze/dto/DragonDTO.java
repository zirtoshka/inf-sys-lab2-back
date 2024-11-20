package org.zir.dragonieze.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.zir.dragonieze.dragon.Color;

import org.zir.dragonieze.dragon.Dragon;
import org.zir.dragonieze.dragon.DragonCharacter;
import org.zir.dragonieze.dragon.DragonHead;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class DragonDTO {
    private long id;
    private String name;
    private Long coordinatesId;
    private java.time.LocalDate creationDate;
    private Long caveId;
    private Long killerId;
    private Integer age;
    private long wingspan;
    private Color color;
    private DragonCharacter character;
    private boolean canEdit;
    private Long userId;
    private int headCount;
    private List<Long> headIds;

    public DragonDTO(Dragon dragon) {
        this.id = dragon.getId();
        this.name = dragon.getName();
        this.coordinatesId = dragon.getCoordinates().getId();
        this.caveId = dragon.getCave().getId();
        if (dragon.getKiller() != null) {
            this.killerId = dragon.getKiller().getId();
        }
        this.age = dragon.getAge();
        this.wingspan = dragon.getWingspan();
        this.color = dragon.getColor();
        this.character = dragon.getCharacter();
        this.canEdit = dragon.getCanEdit();
        this.userId = dragon.getUser().getId();
        this.headCount = dragon.getHeadCount();
        this.headIds = dragon.getHeads().stream()
                .map(DragonHead::getId)
                .collect(Collectors.toList());
    }

}
