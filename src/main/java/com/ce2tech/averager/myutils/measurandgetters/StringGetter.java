package com.ce2tech.averager.myutils.measurandgetters;

import com.ce2tech.averager.model.dto.Measurand;

public class StringGetter extends MeasurandValueGetter {

    @Override
    public Object getValue(Measurand measurand) {

        if (measurand.getErrorValue() != null) {
            return measurand.getErrorValue();
        } else {
            return nextGetter.getValue(measurand);
        }

    }

}
