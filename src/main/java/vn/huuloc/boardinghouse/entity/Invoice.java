package vn.huuloc.boardinghouse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import vn.huuloc.boardinghouse.entity.common.BaseEntity;
import vn.huuloc.boardinghouse.entity.enums.InvoiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static vn.huuloc.boardinghouse.constant.DbConstants.*;
import static vn.huuloc.boardinghouse.util.CommonUtils.defaultBigDecimalIfNull;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoices")
public class Invoice extends BaseEntity {

    @PrePersist
    void prePersist() {
        this.electricityAmount = defaultBigDecimalIfNull(this.electricityAmount);
        this.waterAmount = defaultBigDecimalIfNull(this.waterAmount);
        this.totalServiceFee = defaultBigDecimalIfNull(this.totalServiceFee);
        this.totalAmount = defaultBigDecimalIfNull(this.totalAmount);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contract_id", referencedColumnName = "id")
    private Contract contract;

    @Column(name = "old_electricity_number")
    private double oldElectricityNumber;

    @Column(name = "old_water_number")
    private double oldWaterNumber;

    @Column(name = "new_electricity_number")
    private double newElectricityNumber;

    @Column(name = "new_water_number")
    private double newWaterNumber;

    @Column(name = "usage_electricity_number")
    private double usageElectricityNumber;

    @Column(name = "usage_water_number")
    private double usageWaterNumber;

    @Column(name = "electricity_amount", columnDefinition = DECIMAL_MONEY_DEFAULT_0)
    @ColumnDefault("0")
    private BigDecimal electricityAmount;

    @Column(name = "water_amount", columnDefinition = DECIMAL_MONEY_DEFAULT_0)
    @ColumnDefault("0")
    private BigDecimal waterAmount;

    @Column(name = "total_service_fee", columnDefinition = DECIMAL_MONEY_DEFAULT_0)
    @ColumnDefault("0")
    private BigDecimal totalServiceFee;


    @Column(name = "total_amount", columnDefinition = DECIMAL_MONEY_DEFAULT_0)
    @ColumnDefault("0")
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", columnDefinition = DECIMAL_MONEY_DEFAULT_0)
    @ColumnDefault("0")
    private BigDecimal paidAmount;

    @ManyToMany
    @JoinTable(
            name = "invoice_service_fees",
            joinColumns = @JoinColumn(name = "invoice_id"),
            inverseJoinColumns = @JoinColumn(name = "service_fee_id")
    )
    private List<ServiceFee> serviceFees;

    @Column(name = "print_date")
    private LocalDateTime printDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private InvoiceType type;

    @Column(name = "custom_amount")
    private BigDecimal customAmount;
}
