package org.zir.dragonieze.dragon;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.zir.dragonieze.user.User;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name ="coord")
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private Double x; //Поле не может быть null
    @NotNull
    @DecimalMin(value = "-182", message = "y must be greater than or equal to -182")
    private Float y; //Значение поля должно быть больше -182, Поле не может быть null
    @NotNull
    private boolean canEdit;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
