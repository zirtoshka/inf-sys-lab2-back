package org.zir.dragonieze.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.zir.dragonieze.admin.AdminApplication;
import org.zir.dragonieze.admin.StatusApplication;


import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ApplicationDTO {

    private Long id;
    private LocalDateTime createdAt;
    private StatusApplication status;
    private Long userId;

    public ApplicationDTO(AdminApplication application) {
        this.id = application.getId();
        this.createdAt = application.getCreatedAt();
        this.status = application.getStatus();
        this.userId = application.getUser().getId();

    }
}
