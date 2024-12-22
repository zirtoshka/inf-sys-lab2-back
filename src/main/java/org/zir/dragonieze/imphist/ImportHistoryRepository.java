package org.zir.dragonieze.imphist;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ImportHistoryRepository extends JpaRepository<ImportHistory, Long>, JpaSpecificationExecutor<ImportHistory> {
}
