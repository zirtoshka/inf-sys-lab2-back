package org.zir.dragonieze.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DragonCaveDTO {
    private Long id;
    private Integer numberOfTreasures;
    private boolean canEdit;
}
