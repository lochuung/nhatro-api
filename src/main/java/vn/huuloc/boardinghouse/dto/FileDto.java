package vn.huuloc.boardinghouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileDto extends BaseDto{
    private Long id;
    private String key;
    private String fileName;
    private String extension;
    private String type;

}
