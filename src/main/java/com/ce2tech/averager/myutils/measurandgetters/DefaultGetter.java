package com.ce2tech.averager.myutils.measurandgetters;

import com.ce2tech.averager.model.dto.Measurand;

public class DefaultGetter extends MeasurandValueGetter {

    @Override
    public Object getValue(Measurand measurand) {
        return "Can't get value from cell";
    }
}
