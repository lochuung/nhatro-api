package vn.huuloc.boardinghouse.dto.sort.filter;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.cnj.shared.sortfilter.request.SearchRequest;
import vn.huuloc.boardinghouse.enums.ContractStatus;
import vn.huuloc.boardinghouse.enums.InvoiceType;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContractSearchRequest extends SearchRequest {
    private String roomCode;
    private ContractStatus status;
}
