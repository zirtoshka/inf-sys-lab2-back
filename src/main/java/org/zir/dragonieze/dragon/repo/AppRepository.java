package org.zir.dragonieze.dragon.repo;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.zir.dragonieze.admin.AdminApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppRepository extends JpaRepository<AdminApplication, Long>, JpaSpecificationExecutor<AdminApplication> {
    Optional<AdminApplication> findById(Long id);
    List<AdminApplication> findAll();

}
