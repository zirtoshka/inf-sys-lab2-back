package org.zir.dragonieze.dragon.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.zir.dragonieze.dragon.Location;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long>, JpaSpecificationExecutor<Location> {
    List<Location> findByUserId(Long userId);


}