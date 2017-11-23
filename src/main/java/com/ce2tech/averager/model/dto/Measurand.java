package com.ce2tech.averager.model.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class Measurand {

    private String component;
    private LocalDate dateValue;
    private LocalTime timeValue;
    private Double numericValue;
    private String errorValue;

    public Measurand(String component, LocalDate dateValue) {
        this.component = component;
        this.dateValue = dateValue;
    }
    public Measurand(String component, LocalTime timeValue) {
        this.component = component;
        this.timeValue = timeValue;
    }
    public Measurand(String component, Double numericValue) {
        this.component = component;
        this.numericValue = numericValue;
    }
    public Measurand(String component, String errorValue) {
        this.component = component;
        this.errorValue = errorValue;
    }
    public Measurand(Measurand measurandToCopy) {
        this.component = measurandToCopy.getComponent();
        this.dateValue = measurandToCopy.getDateValue();
        this.timeValue = measurandToCopy.getTimeValue();
        this.numericValue = measurandToCopy.getNumericValue();
        this.errorValue = measurandToCopy.getErrorValue();
    }

}
