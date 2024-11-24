package vn.huuloc.boardinghouse.model.projection;

import lombok.*;
import vn.huuloc.boardinghouse.model.entity.Contract;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ContractWithLatestNumberIndex {
    private Double latestElectricityIndex;
    private Double latestWaterIndex;
    private Contract contract;
}
