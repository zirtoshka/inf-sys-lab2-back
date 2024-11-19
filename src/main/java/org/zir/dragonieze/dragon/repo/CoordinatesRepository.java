package org.zir.dragonieze.dragon.repo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.zir.dragonieze.dragon.Coordinates;

import java.util.List;
import java.util.Optional;

public interface CoordinatesRepository extends JpaRepository<Coordinates, Long>, JpaSpecificationExecutor<Coordinates> {
    List<Coordinates> findByUserId(Long userId);

    Optional<Coordinates> findByIdAndUserId(Long id, Long userId);

    List<Coordinates> findAll();

}
