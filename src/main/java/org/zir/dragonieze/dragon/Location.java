package org.zir.dragonieze.dragon;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zir.dragonieze.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Location implements GeneralEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private float x;
    @NotNull
    private Integer y; //Поле не может быть null
    @NotNull
    private Float z; //Поле не может быть null
    @NotNull
    private String name;//Поле не может быть null
    @NotNull
    private boolean canEdit;

    @Override
    public boolean getCanEdit() {
        return canEdit;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @OneToMany(mappedBy = "location")
    @JsonManagedReference
    private List<Person> persons = new ArrayList<>();
}
