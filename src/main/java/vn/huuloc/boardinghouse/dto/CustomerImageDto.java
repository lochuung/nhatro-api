package vn.huuloc.boardinghouse.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerImageDto {
    private Long id;
    private String imageKey;
    private String fileName;
    private String fileType;
}