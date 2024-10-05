package vn.huuloc.boardinghouse.dto.sort.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.huuloc.boardinghouse.enums.RoomDisplayType;
import vn.huuloc.boardinghouse.enums.RoomStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoomSearchRequest extends SearchRequest {
    private String displayType;
}
