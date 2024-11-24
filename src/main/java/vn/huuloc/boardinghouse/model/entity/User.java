package vn.huuloc.boardinghouse.model.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.huuloc.boardinghouse.model.entity.common.BaseEntity;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "username", unique = true)
    private String username;
    private String password;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "name")
    private String fullName;
    private String birthday;
    private String status;
    private boolean enabled;
    private boolean isUsing2FA;
    private String secret;
}
