package org.zir.dragonieze.dragon.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zir.dragonieze.dragon.Coordinates;
import org.zir.dragonieze.dragon.DragonCave;

import java.util.List;

public interface DragonCaveRepository extends JpaRepository<DragonCave, Long> {
    List<DragonCave> findByUserId(Long userId);

}
