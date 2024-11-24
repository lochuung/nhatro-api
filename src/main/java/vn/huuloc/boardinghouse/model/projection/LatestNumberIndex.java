package vn.huuloc.boardinghouse.model.projection;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LatestNumberIndex {
    private Double latestElectricityIndex;
    private Double latestWaterIndex;
}
