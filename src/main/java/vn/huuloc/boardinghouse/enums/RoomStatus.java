package vn.huuloc.boardinghouse.enums;

import lombok.Getter;

@Getter
public enum RoomStatus {
    AVAILABLE("Trống"), RENTED("Đã cho thuê"), MAINTENANCE("Bảo trì");
    final String value;

    RoomStatus(String value) {
        this.value = value;
    }
}
