package org.zir.dragonieze.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.zir.dragonieze.dragon.Coordinates;

@Getter
@Setter
@AllArgsConstructor
public class CoordinatesDTO {
    private long id;
    private double x;
    private float y;
    private boolean canEdit;

    public CoordinatesDTO(Coordinates coordinates) {
        this.id = coordinates.getId();
        this.x = coordinates.getX();
        this.y = coordinates.getY();
        this.canEdit = coordinates.isCanEdit();
    }
}
