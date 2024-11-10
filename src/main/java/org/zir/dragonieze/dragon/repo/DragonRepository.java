package org.zir.dragonieze.dragon.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zir.dragonieze.dragon.Dragon;

import java.util.List;
import java.util.Optional;


public interface DragonRepository extends JpaRepository<Dragon, Long> {
    List<Dragon> findByUserId(Long userId);
    Optional<Dragon> findByIdAndUserId(Long id, Long userId);

}
