package com.ce2tech.averager.myutils;

import com.ce2tech.averager.model.dto.Measurand;

import java.time.LocalTime;
import java.util.List;

public class MeasurandValueGetter {

    public static LocalTime getSampleTime(List<Measurand> sample) {
        for (Measurand measurand : sample) {
            if (getValue(measurand) instanceof LocalTime) {
                return measurand.getTimeValue();
            }
        }
        throw new RuntimeException("Sample doesn't contain time");
    }

    public static Object getValue(Measurand measurand) {
        if (measurand.getNumericValue() != null) {
            return measurand.getNumericValue();
        } else if (measurand.getDateValue() != null) {
            return measurand.getDateValue();
        } else if (measurand.getTimeValue() != null) {
            return measurand.getTimeValue();
        } else if (measurand.getErrorValue() != null) {
            return measurand.getErrorValue();
        } else {
            return "Can't get value from cell";
        }
    }

}
