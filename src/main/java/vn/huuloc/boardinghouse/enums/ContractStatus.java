package vn.huuloc.boardinghouse.enums;

public enum ContractStatus {
    OPENING("Opening"), CLOSED("Closed");
    final String value;

    ContractStatus(String value) {
        this.value = value;
    }
}
