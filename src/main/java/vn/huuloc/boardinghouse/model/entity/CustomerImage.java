package vn.huuloc.boardinghouse.model.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.huuloc.boardinghouse.model.entity.common.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "customer_images")
public class CustomerImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "image_key", nullable = false, unique = true)
    private String imageKey;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
