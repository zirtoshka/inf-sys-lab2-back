package org.zir.dragonieze.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.zir.dragonieze.dragon.Color;
import org.zir.dragonieze.dragon.Country;
import org.zir.dragonieze.dragon.Person;

@AllArgsConstructor
@Setter
@Getter
public class PersonDTO {
    private long id;
    private String name;
    private Color eyeColor;
    private Color hairColor;
    private LocationDTO location;
    private int height;
    private String passportID;
    private Country nationality;
    private boolean canEdit;

    public PersonDTO(Person person) {
        this.id = person.getId();
        this.name = person.getName();
        this.eyeColor = person.getEyeColor();
        this.hairColor = person.getHairColor();
        this.location = new LocationDTO(person.getLocation());
        this.height = person.getHeight();
        this.passportID = person.getPassportID();
        this.nationality = person.getNationality();
        this.canEdit = person.isCanEdit();
    }
}
