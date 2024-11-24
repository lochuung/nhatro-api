package vn.huuloc.boardinghouse.model.entity.settings;

import jakarta.persistence.*;
import lombok.*;
import vn.huuloc.boardinghouse.model.entity.common.BaseEntity;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = Setting.SETTING_TABLE_NAME)
public class Setting extends BaseEntity {
    public static final String SETTING_TABLE_NAME = "settings";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "key_name", unique = true)
    private String key;
    @Column(name = "value")
    private String value;
}
