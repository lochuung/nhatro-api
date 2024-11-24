package vn.huuloc.boardinghouse.model.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.huuloc.boardinghouse.model.entity.common.BaseEntity;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "customers")
public class Customer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "phone_number", unique = true)
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "name")
    private String name;
    @Column(name = "name2")
    private String name2;
    private String birthday;
    private String address;
    private String description;
    private boolean enabled;

    @Column(name = "id_number", unique = true)
    private String idNumber;
    @Column(name = "id_place")
    private String idPlace;
    @Column(name = "id_date")
    private String idDate;

    @Column(name = "bank_name")
    private String bankName;
    @Column(name = "bank_number")
    private String bankNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<CustomerImage> customerImages;

    @OneToMany(mappedBy = "customer")
    private List<ContractCustomerLinked> contractCustomerLinkeds;
}
