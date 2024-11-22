package vn.huuloc.boardinghouse.dto.sort.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.cnj.shared.sortfilter.request.SearchRequest;
import vn.huuloc.boardinghouse.enums.ContractStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContractSearchRequest extends SearchRequest {
    private String roomCode;
    private ContractStatus status;
}
