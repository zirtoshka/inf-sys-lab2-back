package org.zir.dragonieze.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CoordinatesDTO {
    private long id;
    private double x;
    private float y;
    private boolean canEdit;
}
