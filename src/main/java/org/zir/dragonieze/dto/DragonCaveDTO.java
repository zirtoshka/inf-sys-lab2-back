package org.zir.dragonieze.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.zir.dragonieze.dragon.DragonCave;

@Getter
@Setter
@AllArgsConstructor
public class DragonCaveDTO {
    private Long id;
    private Integer numberOfTreasures;
    private boolean canEdit;

    public DragonCaveDTO(DragonCave dragonCave) {
        this.id = dragonCave.getId();
        this.numberOfTreasures = dragonCave.getNumberOfTreasures();
        this.canEdit = dragonCave.isCanEdit();
    }
}
