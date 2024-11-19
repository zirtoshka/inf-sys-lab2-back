package org.zir.dragonieze.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zir.dragonieze.admin.AdminApplication;
import org.zir.dragonieze.admin.StatusApplication;
import org.zir.dragonieze.admin.UpdateAppStatusRequest;
import org.zir.dragonieze.dragon.repo.*;
import org.zir.dragonieze.user.Role;
import org.zir.dragonieze.user.User;

@Service
public class AdminService {
    private final BaseService baseService;
    private final AppRepository appRepository;


    @Autowired
    public AdminService(BaseService baseService,AppRepository appRepository) {
        this.baseService = baseService;
        this.appRepository = appRepository;
    }

    public void changeApplicationStatus(UpdateAppStatusRequest request) {
        AdminApplication application = appRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        switch(request.getStatus()) {
            case APPROVED -> {
                User user = application.getUser();
                if (!user.getRole().toString().contains("ADMIN")) {
                    user.setRole(Role.ADMIN);
                    baseService.userRepository.save(user);
                }
                application.setStatus(StatusApplication.CLOSE);
            }
            case CANCELED -> application.setStatus(StatusApplication.CLOSE);
            default -> throw new IllegalArgumentException("Invalid status value");
        }

        appRepository.save(application);
    }

}
