package vn.huuloc.boardinghouse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SoftDelete;
import vn.huuloc.boardinghouse.constant.DbConstants;
import vn.huuloc.boardinghouse.entity.common.BaseEntity;
import vn.huuloc.boardinghouse.enums.RoomStatus;

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
@Table(name = "rooms", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code", "branch_id"})
})
@SoftDelete
public class Room extends BaseEntity {
    @PrePersist
    void prePersist() {
        this.status = this.status == null ? RoomStatus.AVAILABLE : this.status;
        this.price = defaultBigDecimalIfNull(this.price);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, columnDefinition = DbConstants.DECIMAL_MONEY_DEFAULT_0)
    private BigDecimal price;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'AVAILABLE'")
    private RoomStatus status;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "type")
    private String type;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @OneToMany(mappedBy = "room")
    @JsonIgnore
    private List<Contract> contracts;
}
