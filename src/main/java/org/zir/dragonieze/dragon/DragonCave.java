package org.zir.dragonieze.dragon;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.zir.dragonieze.user.User;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "cave")
public class DragonCave implements GeneralEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numberOfTreasures; // может быть null,  больше 0
    @NotNull
    private boolean canEdit;
    @Override
    public boolean getCanEdit() {
        return canEdit;
    }
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cave",cascade = CascadeType.ALL)
    private List<Dragon> dragonCaves = new ArrayList<>();
}
