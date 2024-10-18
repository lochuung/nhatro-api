package vn.huuloc.boardinghouse.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Map;

@UtilityClass
public class CurrencyUtils {

    public static String getTienChu(BigDecimal totalFinalAmount) {
        String unit = "đồng";
        if (totalFinalAmount == null || totalFinalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return "không đồng";
        }
        if (totalFinalAmount.compareTo(BigDecimal.ZERO) < 0) {
            return "âm " + getTienChu(totalFinalAmount.abs());
        }
        Map<Integer, String> numberMap = Map.of(
                0, "không",
                1, "một",
                2, "hai",
                3, "ba",
                4, "bốn",
                5, "năm",
                6, "sáu",
                7, "bảy",
                8, "tám",
                9, "chín"
        );
        String[] unitMap = {"", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ", "tỷ tỷ"};
        StringBuilder tienChu = new StringBuilder();
        String totalFinalAmountStr = totalFinalAmount.toString();
        String[] split = totalFinalAmountStr.split("\\.");
        String integerPart = split[0];
        int length = integerPart.length();
        int unitIndex = 0;
        for (int i = length - 1; i >= 0; i -= 3) {
            int end = i;
            int start = Math.max(0, i - 2);
            String part = integerPart.substring(start, end + 1);
            int partInt = Integer.parseInt(part);
            if (partInt > 0) {
                String partStr = "";
                int hundred = partInt / 100;
                int ten = (partInt % 100) / 10;
                int one = partInt % 10;
                if (hundred > 0) {
                    partStr += numberMap.get(hundred) + " trăm";
                }
                if (ten > 0) {
                    if (ten == 1) {
                        partStr += " mười";
                    } else {
                        partStr += " " + numberMap.get(ten) + " mươi";
                    }
                }
                if (one > 0) {
                    if (ten == 0 && hundred > 0) {
                        partStr += " lẻ";
                    }
                    if (one == 5 && ten >= 1) {
                        partStr += " lăm";
                    } else {
                        partStr += " " + numberMap.get(one);
                    }
                }
                partStr += " " + unitMap[unitIndex];

                tienChu.insert(0, partStr + " ");
            }

            if (unitIndex + 1 < unitMap.length)
                unitIndex++;
        }

        // remove spare space

        while (tienChu.charAt(0) == ' ') {
            tienChu.deleteCharAt(0);
        }

        while (tienChu.charAt(tienChu.length() - 1) == ' ') {
            tienChu.deleteCharAt(tienChu.length() - 1);
        }

        return StringUtils.capitalize(tienChu + " " + unit);
    }
}
