package vn.huuloc.boardinghouse.entity.enums;

public enum ContractStatus {
    ACTIVE("Đang thuê"), INACTIVE("Hết hạn"), CANCEL("Hủy");
    final String value;

    ContractStatus(String value) {
        this.value = value;
    }
}
