package vn.huuloc.boardinghouse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import vn.huuloc.boardinghouse.constant.DbConstants;
import vn.huuloc.boardinghouse.entity.common.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

import static vn.huuloc.boardinghouse.util.CommonUtils.defaultBigDecimalIfNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_fees")
public class ServiceFee extends BaseEntity {
    @PrePersist
    void prePersist() {
        this.unitPrice = defaultBigDecimalIfNull(this.unitPrice);
    }

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true)
    private String code;

    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "unit_price", columnDefinition = DbConstants.DECIMAL_MONEY_DEFAULT_0)
    @ColumnDefault("0")
    private BigDecimal unitPrice;

    @ManyToMany(mappedBy = "serviceFees")
    private List<Invoice> invoices;

    @Column(name = "is_active")
    private boolean active;
}
