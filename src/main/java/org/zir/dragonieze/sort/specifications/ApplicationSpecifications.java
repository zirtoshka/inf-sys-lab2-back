package org.zir.dragonieze.sort.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.zir.dragonieze.admin.AdminApplication;
import org.zir.dragonieze.admin.StatusApplication;

@Component
public class ApplicationSpecifications extends DataSpecifications {
    public static Specification<AdminApplication> hasUserId(Long userId) {
        return hasField("user.id", userId);
    }

    public static Specification<AdminApplication> hasId(Long id) {
        return hasField("id", id);
    }

    public static Specification<AdminApplication> hasStatus(StatusApplication status) {
        return hasField("status", status);
    }

    public static Specification<AdminApplication> hasCreatedAt(String date) {
        return hasField("createdAt", date);
    }

}
