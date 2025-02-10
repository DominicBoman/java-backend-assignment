package com.building.temperaturecontrol.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TemperatureValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TemperatureConstraint {
    String message() default "Temperature must be between 0.0°C and 50.0°C";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 