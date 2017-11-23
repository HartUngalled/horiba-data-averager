package com.ce2tech.averager.myutils.measurandgetters;

import com.ce2tech.averager.model.dto.Measurand;

import java.time.LocalTime;
import java.util.List;

public class TimeGetter extends MeasurandValueGetter {

    @Override
    public Object getValue(Measurand measurand) {

        if (measurand.getTimeValue() != null) {
            return measurand.getTimeValue();
        } else {
            return nextGetter.getValue(measurand);
        }

    }

}
