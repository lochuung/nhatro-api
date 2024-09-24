package vn.huuloc.boardinghouse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import vn.huuloc.boardinghouse.constant.DbConstants;
import vn.huuloc.boardinghouse.entity.common.BaseEntity;
import vn.huuloc.boardinghouse.entity.enums.ContractStatus;

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

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "number_of_people")
    private Integer numberOfPeople;

    @Column(name = "price", columnDefinition = DbConstants.DECIMAL_MONEY_DEFAULT_0)
    private BigDecimal price;

    @Column(name = "deposit", columnDefinition = DbConstants.DECIMAL_MONEY_DEFAULT_0)
    private BigDecimal deposit;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column(name = "note")
    private String note;

    @Column(name = "code", unique = true)
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private String code; // uuid

    @OneToMany(mappedBy = "contract")
    private List<ContractCustomerLinked> contractCustomerLinkeds;
}
