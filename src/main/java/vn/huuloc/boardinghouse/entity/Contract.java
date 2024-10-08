package vn.huuloc.boardinghouse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import vn.huuloc.boardinghouse.constant.DbConstants;
import vn.huuloc.boardinghouse.entity.common.BaseEntity;
import vn.huuloc.boardinghouse.enums.ContractStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contracts")
public class Contract extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "number_of_people")
    private Integer numberOfPeople;

    @Column(name = "checkin_electric_number")
    private double checkinElectricNumber;

    @Column(name = "checkin_water_number")
    private double checkinWaterNumber;

    @Column(name = "room_status")
    private String roomStatus;

    @Column(name = "price", columnDefinition = DbConstants.DECIMAL_MONEY_DEFAULT_0)
    private BigDecimal price;

    @Column(name = "deposit", columnDefinition = DbConstants.DECIMAL_MONEY_DEFAULT_0)
    private BigDecimal deposit;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column(name = "note")
    private String note;

    @ManyToMany
    @JoinTable(
            name = "contract_service_fees",
            joinColumns = @JoinColumn(name = "contract_id"),
            inverseJoinColumns = @JoinColumn(name = "service_fee_id")
    )
    private List<ServiceFee> serviceFees;

    @Column(name = "code", unique = true)
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private String code; // uuid

    @OneToMany(mappedBy = "contract")
    private List<ContractCustomerLinked> contractCustomerLinkeds;
}
