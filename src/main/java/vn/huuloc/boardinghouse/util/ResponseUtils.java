package vn.huuloc.boardinghouse.util;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class ResponseUtils {
    public static Map<String, String> success() {
        return Map.of("message", "success");
    }
}
