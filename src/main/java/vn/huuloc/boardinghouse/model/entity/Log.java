package vn.huuloc.boardinghouse.model.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.huuloc.boardinghouse.model.entity.common.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "Log")
@Table(name = "logs")
public class Log extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "event")
    private String event;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "username")
    private String username;

    @Column(name = "old_data", columnDefinition = "TEXT")
    private String oldData;

    @Column(name = "new_data", columnDefinition = "TEXT")
    private String newData;
}
