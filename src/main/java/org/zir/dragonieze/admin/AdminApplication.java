package org.zir.dragonieze.admin;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zir.dragonieze.user.User;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
public class AdminApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    @Enumerated
    private StatusApplication status;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
