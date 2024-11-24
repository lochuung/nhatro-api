package vn.huuloc.boardinghouse.util;

import lombok.experimental.UtilityClass;
import vn.huuloc.boardinghouse.model.entity.Invoice;

import java.math.BigDecimal;

@UtilityClass
public class InvoiceUtils {

    public static BigDecimal calculateElectricityAmount(Invoice invoice) {
        return invoice.getElectricityUnitPrice().multiply(BigDecimal.valueOf(invoice.getNewElectricityNumber() - invoice.getOldElectricityNumber()));
    }

    public static BigDecimal calculateWaterAmount(Invoice invoice) {
        return invoice.getWaterUnitPrice().multiply(BigDecimal.valueOf(invoice.getNewWaterNumber() - invoice.getOldWaterNumber()));
    }
}
