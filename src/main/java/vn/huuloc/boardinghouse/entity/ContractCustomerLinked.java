package vn.huuloc.boardinghouse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import vn.huuloc.boardinghouse.entity.common.BaseEntity;

import java.time.LocalDate;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contract_customer_linked")
public class ContractCustomerLinked extends BaseEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "customer_id")
    private Long customerId;

    // mapping to contract
    @ManyToOne
    @JoinColumn(name = "contract_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Contract contract;

    // mapping to customer
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Customer customer;

    @Column(name = "is_owner")
    @ColumnDefault("false")
    private boolean isOwner;

    @Column(name = "stand_in_contract")
    @ColumnDefault("false")
    private boolean isStandInContract;

    @Column(name = "is_renter")
    @ColumnDefault("false")
    private boolean isRenter;

    @Column(name = "checkout_date")
    private LocalDate checkoutDate;
}
