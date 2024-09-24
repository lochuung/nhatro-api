package vn.huuloc.boardinghouse.entity.enums;

public enum RoomStatus {
    AVAILABLE("Trống"), OCCUPIED("Đã cho thuê"), MAINTENANCE("Bảo trì");
    final String value;

    RoomStatus(String value) {
        this.value = value;
    }
}
