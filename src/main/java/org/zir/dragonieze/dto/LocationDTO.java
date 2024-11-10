package org.zir.dragonieze.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LocationDTO {
    private long id;
    private float x;
    private Integer y;
    private Float z;
    private String name;
    private boolean canEdit;

}
