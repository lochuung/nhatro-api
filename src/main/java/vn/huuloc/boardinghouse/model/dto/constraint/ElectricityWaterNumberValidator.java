package vn.huuloc.boardinghouse.model.dto.constraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.huuloc.boardinghouse.model.dto.request.InvoiceRequest;

public class ElectricityWaterNumberValidator implements ConstraintValidator<ValidElectricityWaterNumber, InvoiceRequest> {

    @Override
    public boolean isValid(InvoiceRequest invoiceRequest, ConstraintValidatorContext context) {
        if (invoiceRequest == null) {
            return true; // Let other @NotNull annotations handle null cases
        }

        boolean validElectricity = invoiceRequest.getNewElectricityNumber() > invoiceRequest.getOldElectricityNumber();
        boolean validWater = invoiceRequest.getNewWaterNumber() > invoiceRequest.getOldWaterNumber();

        if (!validElectricity || !validWater) {
            // Customize the error message to show both electricity and water issues
            context.disableDefaultConstraintViolation();
            if (!validElectricity) {
                context.buildConstraintViolationWithTemplate("Số điện mới phải lớn hơn hoặc bằng số điện cũ")
                        .addPropertyNode("newElectricityNumber")
                        .addConstraintViolation();
            }
            if (!validWater) {
                context.buildConstraintViolationWithTemplate("Số nước mới phải lớn hơn hoặc bằng số nước cũ")
                        .addPropertyNode("newWaterNumber")
                        .addConstraintViolation();
            }
            return false;
        }

        return true;
    }
}