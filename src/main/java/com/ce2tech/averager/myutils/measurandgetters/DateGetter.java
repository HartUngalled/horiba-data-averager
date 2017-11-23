package com.ce2tech.averager.myutils.measurandgetters;

import com.ce2tech.averager.model.dto.Measurand;

public class DateGetter extends MeasurandValueGetter {

    @Override
    public Object getValue(Measurand measurand) {

        if (measurand.getDateValue() != null) {
            return measurand.getDateValue();
        } else {
            return nextGetter.getValue(measurand);
        }

    }

}
