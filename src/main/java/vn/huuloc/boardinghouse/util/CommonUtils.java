package vn.huuloc.boardinghouse.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
@Slf4j
public class CommonUtils {
    // Sử dụng SecureRandom để tạo chuỗi số ngẫu nhiên
    private static final SecureRandom random = new SecureRandom();

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


    public static String generateCode() {
        Date now = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyMMdd");
        String datetime = ft.format(now);
        String randomNumeric = String.format("%04d", random.nextInt(10000));

        return datetime + StringUtils.leftPad(randomNumeric, 4, "0");
    }
}
