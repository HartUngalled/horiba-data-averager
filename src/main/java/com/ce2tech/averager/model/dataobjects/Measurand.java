package com.ce2tech.averager.model.dataobjects;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class Measurand {

    private String component;
    private LocalTime timeValue;
    private LocalDate dateValue;
    private Double numericValue;
    private String errorValue;

    public Measurand(Measurand measurandToCopy) {
        this.component = measurandToCopy.getComponent();
        this.dateValue = measurandToCopy.getDateValue();
        this.timeValue = measurandToCopy.getTimeValue();
        this.numericValue = measurandToCopy.getNumericValue();
        this.errorValue = measurandToCopy.getErrorValue();
    }

    public Measurand(String component, Object value) {
        this.component = component;

        if (value instanceof LocalTime)
            timeValue = (LocalTime) value;

        else if (value instanceof LocalDate)
            dateValue = (LocalDate) value;

        else if (value instanceof Double)
            numericValue = (Double) value;

        else if (value instanceof String)
            errorValue = (String) value;
    }

}
