package com.ce2tech.averager.myutils.measurandgetters;

import com.ce2tech.averager.model.dto.Measurand;

public class NumericGetter extends MeasurandValueGetter {

    @Override
    public Object getValue(Measurand measurand) {

        if (measurand.getNumericValue() != null) {
            return measurand.getNumericValue();
        } else {
            return nextGetter.getValue(measurand);
        }

    }

}
