package org.zir.dragonieze.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.zir.dragonieze.dragon.Location;

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

    public LocationDTO(Location location) {
        this.id = location.getId();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.name = location.getName();
        this.canEdit = location.isCanEdit();
    }

}
