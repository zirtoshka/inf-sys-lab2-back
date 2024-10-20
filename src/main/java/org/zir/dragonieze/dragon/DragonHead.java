package org.zir.dragonieze.dragon;

import jakarta.persistence.*;

@Entity
public class DragonHead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double eyesCount;


    @ManyToOne(optional = false) // хотя бы одна у дракона
    @JoinColumn(name = "dragon_id", nullable = false)
    private Dragon dragon;

}
