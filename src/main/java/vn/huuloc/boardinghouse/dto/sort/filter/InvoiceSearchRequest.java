package vn.huuloc.boardinghouse.dto.sort.filter;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.cnj.shared.sortfilter.request.SearchRequest;
import vn.huuloc.boardinghouse.enums.InvoiceType;

@EqualsAndHashCode(callSuper = true)
@Data
public class InvoiceSearchRequest extends SearchRequest {
    private Long roomId;
    // mm/yyyy
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{4}$")
    private String month;
    private Boolean isPaid;
    private InvoiceType type;
    private String keyword;
}
