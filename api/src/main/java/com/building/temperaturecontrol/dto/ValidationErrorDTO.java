package com.building.temperaturecontrol.dto;

import java.util.Map;

public class ValidationErrorDTO {
    private Map<String, String> errors;

    public ValidationErrorDTO(Map<String, String> errors) {
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
