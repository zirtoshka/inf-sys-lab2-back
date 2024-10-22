package org.zir.dragonieze.dragon.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zir.dragonieze.dragon.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {}