package vn.huuloc.boardinghouse.dto.sort.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoomSearchRequest extends SearchRequest {
    private String displayType;
}
