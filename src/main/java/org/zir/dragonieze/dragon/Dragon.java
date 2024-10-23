package org.zir.dragonieze.dragon;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name ="dragon")
public class Dragon {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id; // больше 0, уникальным, генерироваться автоматически

    @NotBlank(message = "Name is mandatory")
    private String name; // не  null, Строка не может быть пустой

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "coord_id")
    private Coordinates coordinates; // не null
    @NotNull
    private java.time.LocalDate creationDate; // не null, генерироваться автоматически


    @NotNull
    @ManyToOne(cascade = CascadeType.ALL,optional = false) // Много драконов могут относиться к одной пещере
    @JoinColumn(name = "cave_id", nullable = false, unique = true) // один дракон только в одной пещере
    private DragonCave cave; // не null

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "killer_id", nullable = true)
    private Person killer; // может быть null

    @NotNull
    @Positive
    private Integer age; // больше 0,  может быть null
    @Positive
    private long wingspan; // больше 0
    @NotNull
    private Color color; // не null
    @NotNull
    private DragonCharacter character; // не null


}
