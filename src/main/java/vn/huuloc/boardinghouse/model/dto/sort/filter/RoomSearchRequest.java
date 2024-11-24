package vn.huuloc.boardinghouse.model.dto.sort.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.cnj.shared.sortfilter.request.SearchRequest;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoomSearchRequest extends SearchRequest {
    private String displayType;
}
