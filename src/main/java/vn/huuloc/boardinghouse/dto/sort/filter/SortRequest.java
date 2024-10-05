package vn.huuloc.boardinghouse.dto.sort.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.huuloc.boardinghouse.enums.sort.filter.SortDirection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SortRequest {

    private String key;

    private SortDirection direction;

}