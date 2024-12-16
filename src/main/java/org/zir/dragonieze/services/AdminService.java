package org.zir.dragonieze.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zir.dragonieze.admin.AdminApplication;
import org.zir.dragonieze.admin.StatusApplication;
import org.zir.dragonieze.admin.UpdateAppStatusRequest;
import org.zir.dragonieze.dragon.repo.*;
import org.zir.dragonieze.openam.api.OpenAmRestApiClient;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;

import org.zir.dragonieze.user.Role;
import org.zir.dragonieze.user.User;
import org.zir.dragonieze.user.UserRepository;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class AdminService {
    private final AppRepository appRepository;
    private final UserRepository userRepository;
    private final OpenAmRestApiClient openAmApi;

    public void changeApplicationStatus(OpenAmUserPrincipal admin, UpdateAppStatusRequest request) {
        AdminApplication application = appRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        switch(request.getStatus()) {
            case APPROVED -> {
                User user = application.getUser();
                if (!openAmApi.isMemberOf(admin.getAuthCookie(), user.getDn(), Role.ADMIN.getFullName())) {
                    openAmApi.addUserToGroup(
                            admin.getAuthCookie(),
                            user.getDn(),
                            Role.ADMIN.getFullName()
                    );
                    userRepository.save(user);
                }
                application.setStatus(StatusApplication.CLOSE);
            }
            case CANCELED -> application.setStatus(StatusApplication.CANCELED);
            default -> throw new IllegalArgumentException("Invalid status value");
        }
        appRepository.save(application);
    }
}
