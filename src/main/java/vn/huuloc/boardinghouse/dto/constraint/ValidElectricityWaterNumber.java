package vn.huuloc.boardinghouse.dto.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ElectricityWaterNumberValidator.class)
@Target({ ElementType.TYPE }) // Apply at the class level
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidElectricityWaterNumber {
    String message() default "Số điện và số nước mới phải lớn hơn hoặc bằng số cũ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}