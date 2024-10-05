package vn.huuloc.boardinghouse.enums;

import lombok.Getter;

@Getter
public enum RoomDisplayType {
    ALL("all"),
    RENTED("rented"),
    AVAILABLE("available");

    final String value;

    RoomDisplayType(String value) {
        this.value = value;
    }
}
