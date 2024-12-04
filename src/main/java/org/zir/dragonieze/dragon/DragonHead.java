package org.zir.dragonieze.dragon;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.zir.dragonieze.user.User;

@Getter
@Setter
@Entity
@Table(name = "head")
public class DragonHead implements GeneralEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double eyesCount;
    @NotNull
    private boolean canEdit;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dragon_id")
    private Dragon dragon;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}
