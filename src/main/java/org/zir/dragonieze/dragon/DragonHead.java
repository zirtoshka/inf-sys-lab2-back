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
public class DragonHead implements EditableEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double eyesCount;
    @NotNull
    private boolean canEdit;


    @ManyToOne(cascade = CascadeType.ALL) // хотя бы одна у дракона
    @JoinColumn(name = "dragon_id")
    private Dragon dragon;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public boolean getCanEdit() {
        return canEdit;
    }
}
