package org.zir.dragonieze.dragon;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class DragonHead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double eyesCount;
    @NotNull
    private boolean canEdit;


    @ManyToOne(cascade = CascadeType.ALL,optional = false) // хотя бы одна у дракона
    @JoinColumn(name = "dragon_id", nullable = false)
    private Dragon dragon;

}
