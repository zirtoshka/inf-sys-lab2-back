package org.zir.dragonieze.dragon;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.zir.dragonieze.user.User;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name ="coord")
public class Coordinates implements GeneralEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Double x; //Поле не может быть null
    @NotNull
    @DecimalMin(value = "-182", message = "y must be greater than or equal to -182")
    private Float y; //Значение поля должно быть больше -182, Поле не может быть null
    @NotNull
    private boolean canEdit;
    @Override
    public boolean getCanEdit() {
        return canEdit;
    }

    @OneToMany(mappedBy = "coordinates", cascade = CascadeType.ALL)
    private List<Dragon> dragons = new ArrayList<>();




    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
