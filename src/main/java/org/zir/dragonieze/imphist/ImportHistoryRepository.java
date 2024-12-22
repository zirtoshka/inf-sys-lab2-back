package org.zir.dragonieze.imphist;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface ImportHistoryRepository extends JpaRepository<ImportHistory, Long> {
    List<ImportHistory> findByUserId(Long userId);
}
