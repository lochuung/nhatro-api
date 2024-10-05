package vn.huuloc.boardinghouse.enums;

import lombok.Getter;

@Getter
public enum CustomerFileType {
    CCCD("cccd");
    final String value;

    CustomerFileType(String value) {
        this.value = value;
    }

    public static boolean isFileTypeValid(String fileType) {
        for (CustomerFileType c : values()) {
            if (c.getValue().equalsIgnoreCase(fileType)) {
                return true;
            }
        }
        return false;
    }
}
