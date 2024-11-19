package org.zir.dragonieze.dragon.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.zir.dragonieze.dragon.DragonCave;


import java.util.List;

public interface DragonCaveRepository extends JpaRepository<DragonCave, Long>, JpaSpecificationExecutor<DragonCave> {
    List<DragonCave> findByUserId(Long userId);

}
