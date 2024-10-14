package vn.huuloc.boardinghouse.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.huuloc.boardinghouse.dto.InvoiceDto;
import vn.huuloc.boardinghouse.dto.request.InvoiceRequest;
import vn.huuloc.boardinghouse.entity.Invoice;

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

    InvoiceDto toDto(Invoice invoice);
}
