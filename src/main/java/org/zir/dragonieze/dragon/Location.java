package org.zir.dragonieze.dragon;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private float x;
    @NotNull
    private Integer y; //Поле не может быть null
    @NotNull
    private Float z; //Поле не может быть null
    @NotNull
    private String name; //Поле не может быть null

}
