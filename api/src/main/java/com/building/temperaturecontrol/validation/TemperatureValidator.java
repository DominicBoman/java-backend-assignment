package com.building.temperaturecontrol.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class TemperatureValidator implements ConstraintValidator<TemperatureConstraint, BigDecimal> {
    private static final BigDecimal MIN_TEMP = new BigDecimal("0.0");
    private static final BigDecimal MAX_TEMP = new BigDecimal("50.0");

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.compareTo(MIN_TEMP) >= 0 && value.compareTo(MAX_TEMP) <= 0;
    }
} 