package com.ce2tech.averager.myutils.measurandgetters;

import com.ce2tech.averager.model.dto.Measurand;
import lombok.AccessLevel;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

public abstract class MeasurandValueGetter {

    @Setter(AccessLevel.PROTECTED) protected MeasurandValueGetter nextGetter;

    public static MeasurandValueGetter getChainOfResponsibility() {
        MeasurandValueGetter numericGetter = new NumericGetter();
        MeasurandValueGetter dateGetter = new DateGetter();
        MeasurandValueGetter timeGetter = new TimeGetter();
        MeasurandValueGetter stringGetter = new StringGetter();
        MeasurandValueGetter defaultGetter = new DefaultGetter();

        numericGetter.setNextGetter(dateGetter);
        dateGetter.setNextGetter(timeGetter);
        timeGetter.setNextGetter(stringGetter);
        stringGetter.setNextGetter(defaultGetter);

        return numericGetter;
    }

    public LocalTime getSampleTime(List<Measurand> sample) {
        for (Measurand measurand : sample) {
            if (getValue(measurand) instanceof LocalTime) {
                return measurand.getTimeValue();
            }
        }
        throw new RuntimeException("Sample doesn't contain time");
    }

    abstract public Object getValue(Measurand measurand);

}
