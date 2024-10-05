package vn.huuloc.boardinghouse.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class CommonUtils {
    public static BigDecimal defaultBigDecimalIfNull(BigDecimal bigDecimal) {
        if (null == bigDecimal) {
            return BigDecimal.ZERO;
        }
        return bigDecimal;
    }

    public static BigDecimal removeZeroTrail(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public static boolean isNumber(Object obj) {
        if (obj == null) return false;
        Class<?> clazz = obj.getClass();
        return Number.class.isAssignableFrom(clazz);
    }

    public static boolean isNewRecord(Long id) {
        return id == null || id <= 0;
    }


}
