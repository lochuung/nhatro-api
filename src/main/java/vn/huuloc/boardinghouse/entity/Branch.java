package vn.huuloc.boardinghouse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import vn.huuloc.boardinghouse.entity.common.BaseEntity;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "branches")
public class Branch extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image")
    private String image;

    @Column(name = "lessor_name")
    private String lessorName;

    @Column(name = "lessor_phone")
    private String lessorPhone;

    @Column(name = "lessor_birth")
    private String lessorBirth;

    @Column(name = "lessor_address")
    private String lessorAddress;

    @Column(name = "lessor_cccd")
    private String lessorCCCD;

    @Column(name = "ngay_cap")
    private String ngayCap;

    @Column(name = "noi_cap")
    private String noiCap;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    @ColumnDefault("true")
    private Boolean status;

    @OneToMany(mappedBy = "branch")
    @JsonIgnore
    private List<Room> rooms;

}
