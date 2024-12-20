package org.zir.dragonieze.dragon;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
    @Override
    public boolean getCanEdit() {
        return canEdit;
    }

    @ManyToOne()
    @JoinColumn(name = "dragon_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Dragon dragon;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
