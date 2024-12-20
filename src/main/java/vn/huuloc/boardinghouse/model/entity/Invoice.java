package vn.huuloc.boardinghouse.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;
import vn.huuloc.boardinghouse.model.entity.common.BaseEntity;
import vn.huuloc.boardinghouse.enums.InvoiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static vn.huuloc.boardinghouse.constant.DbConstants.DECIMAL_MONEY_DEFAULT_0;
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

    @UuidGenerator
    @Column(name = "code", unique = true, updatable = false, nullable = false)
    private UUID code;

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

    @Column(name = "electricity_unit_price", columnDefinition = DECIMAL_MONEY_DEFAULT_0)
    @ColumnDefault("0")
    private BigDecimal electricityUnitPrice;

    @Column(name = "water_unit_price", columnDefinition = DECIMAL_MONEY_DEFAULT_0)
    @ColumnDefault("0")
    private BigDecimal waterUnitPrice;

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

    @Column(name = "sub_total", columnDefinition = DECIMAL_MONEY_DEFAULT_0)
    @ColumnDefault("0")
    private BigDecimal subTotal;

    @Column(name = "discount", columnDefinition = DECIMAL_MONEY_DEFAULT_0)
    @ColumnDefault("0")
    private BigDecimal discount;


    @Column(name = "total_amount", columnDefinition = DECIMAL_MONEY_DEFAULT_0)
    @ColumnDefault("0")
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", columnDefinition = DECIMAL_MONEY_DEFAULT_0)
    @ColumnDefault("0")
    private BigDecimal paidAmount;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "invoice_service_fees",
            joinColumns = @JoinColumn(name = "invoice_id"),
            inverseJoinColumns = @JoinColumn(name = "service_fee_id")
    )
    private List<ServiceFee> serviceFees = new ArrayList<>();

    @Column(name = "print_date")
    private LocalDateTime printDate;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private InvoiceType type;

    @Column(name = "custom_amount")
    private BigDecimal roomAmount;

    @Column(name = "other_fee", columnDefinition = DECIMAL_MONEY_DEFAULT_0)
    @ColumnDefault("0")
    private BigDecimal otherFee;

    @Column(name = "other_fee_note")
    private String otherFeeNote;
}
