package vn.huuloc.boardinghouse.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.huuloc.boardinghouse.model.dto.InvoiceDto;
import vn.huuloc.boardinghouse.model.dto.request.InvoiceRequest;
import vn.huuloc.boardinghouse.model.entity.Invoice;
import vn.huuloc.boardinghouse.util.CurrencyUtils;

import java.math.BigDecimal;

@Mapper
public interface InvoiceMapper {
    InvoiceMapper INSTANCE = Mappers.getMapper(InvoiceMapper.class);

    @Mapping(target = "usageElectricityNumber",
            expression = "java(calculateUsage(invoiceRequest.getNewElectricityNumber(), invoiceRequest.getOldElectricityNumber()))")
    @Mapping(target = "usageWaterNumber",
            expression = "java(calculateUsage(invoiceRequest.getNewWaterNumber(), invoiceRequest.getOldWaterNumber()))")
    Invoice toEntity(InvoiceRequest invoiceRequest);

    default double calculateUsage(double newIndex, double oldIndex) {
        return newIndex - oldIndex;
    }

    @Mapping(target = "totalAmountInWords", expression = "java(getTienChu(invoice.getTotalAmount()))")
    InvoiceDto toDto(Invoice invoice);

    default String getTienChu(BigDecimal number) {
        return CurrencyUtils.getTienChu(number);
    }
}
