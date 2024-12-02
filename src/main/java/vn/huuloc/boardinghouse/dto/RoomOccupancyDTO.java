package vn.huuloc.boardinghouse.dto;

import lombok.Data;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomOccupancyDTO {
    private String month;
    private Long occupied;
    private Long available;
}