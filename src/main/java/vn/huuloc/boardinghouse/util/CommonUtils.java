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
}
