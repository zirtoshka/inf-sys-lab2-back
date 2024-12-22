package org.zir.dragonieze.imphist;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.zir.dragonieze.sort.specifications.DataSpecifications;
@Component
public class ImportHistorySpecification extends DataSpecifications{
    public Specification<ImportHistory>  hasUserId(Long userId) {
        return hasField("userId", userId);
    }

    public  Specification<ImportHistory> hasId(Long id) {
        return hasField("id", id);
    }
    public Specification<ImportHistory>  hasImportedCount(Integer count) {
        return hasField("importedCount", count);
    }

    public Specification<ImportHistory>  hasStatus(StatusImport statusImport) {
        return hasField("status", statusImport);
    }
}
