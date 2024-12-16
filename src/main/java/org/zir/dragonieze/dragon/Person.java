package org.zir.dragonieze.dragon;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zir.dragonieze.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class Person implements GeneralEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private String name; //Поле не может быть null, Строка не может быть пустой
    @NotNull
    private Color eyeColor; //Поле не может быть null
    @NotNull
    private Color hairColor; //Поле не может быть null


    @ManyToOne
    @JoinColumn(name = "location_id")
    @JsonBackReference
    private Location location; //Поле может быть null

    @Positive
    private int height; //Значение поля должно быть больше 0
    @NotBlank
    private String passportID; //Строка не может быть пустой, Поле может быть null
    @NotNull
    private Country nationality; //Поле не может быть null

    @NotNull
    private boolean canEdit;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public boolean getCanEdit() {
        return canEdit;
    }

    @OneToMany(mappedBy = "killer")
    private List<Dragon> dragons = new ArrayList<>();

}
