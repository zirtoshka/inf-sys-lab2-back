package org.zir.dragonieze.imphist;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class ImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private Long userId;
    @Column(nullable = true)
    private int importedCount; //can be null
    @Enumerated(EnumType.STRING)
    private StatusImport status;
    @Column(columnDefinition = "TEXT")
    private String fileUrl;

}
