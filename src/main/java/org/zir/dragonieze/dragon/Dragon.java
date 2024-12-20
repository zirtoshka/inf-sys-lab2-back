package org.zir.dragonieze.dragon;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zir.dragonieze.user.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "dragon")
public class Dragon implements GeneralEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // больше 0, уникальным, генерироваться автоматически

    @NotBlank(message = "Name is mandatory")
    private String name; // не  null, Строка не может быть пустой

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "coord_id", nullable = false)
    private Coordinates coordinates; // не null
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate creationDate = LocalDate.now(); // не null, генерироваться автоматически


    @NotNull
    @ManyToOne(optional = false) // Много драконов могут относиться к одной пещере
    @JoinColumn(name = "cave_id", nullable = false)
    private DragonCave cave; // не null

    @ManyToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "killer_id")
    private Person killer; // может быть null

    @Positive
    private Integer age; // больше 0,  может быть null
    @Positive
    private long wingspan; // больше 0
    @NotNull
    private Color color; // не null
    @NotNull
    private DragonCharacter character; // не null
    @NotNull
    private boolean canEdit;


    @OneToMany(mappedBy = "dragon")
    private List<DragonHead> heads = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Override
    public boolean getCanEdit() {
        return canEdit;
    }

    public int getHeadCount() {
        return heads.size();
    }
}
