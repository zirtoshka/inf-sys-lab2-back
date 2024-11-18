package org.zir.dragonieze.dragon;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.zir.dragonieze.user.User;


@Getter
@Setter
@Entity
public class DragonCave implements EditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numberOfTreasures; // может быть null,  больше 0
    @NotNull
    private boolean canEdit;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public boolean getCanEdit() {
        return canEdit;
    }
}
