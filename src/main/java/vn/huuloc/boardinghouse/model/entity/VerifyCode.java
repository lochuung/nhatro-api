package vn.huuloc.boardinghouse.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "verify_codes")
public class VerifyCode {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String code;
    private String type;
    private LocalDateTime expiredAt;
    private boolean used;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
