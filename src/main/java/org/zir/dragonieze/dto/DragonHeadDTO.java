package org.zir.dragonieze.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.zir.dragonieze.dragon.DragonHead;

@Getter
@Setter
@AllArgsConstructor
public class DragonHeadDTO {
    private long id;
    private double eyesCount;
    private boolean canEdit;

    public DragonHeadDTO(DragonHead dragonHead) {
        this.id = dragonHead.getId();
        this.eyesCount = dragonHead.getEyesCount();
        this.canEdit = dragonHead.isCanEdit();
    }
}
