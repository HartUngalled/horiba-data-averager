package com.ce2tech.averager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public enum AcceptableComponents {

    DATE("Data"), TIME("Czas"), NO("NO[ppm]"), NOx("NOx[ppm]"),
    CO("CO[ppm]"), CO2("CO2[vol%]"), O2("O2[vol%]"), SO2("SO2[ppm]"),
    TEMP("Temp");

    @Getter @Setter String description;

    public static boolean isAcceptableMeasurand(String headerCell) {
        for (AcceptableComponents component : AcceptableComponents.values()) {
            if ( headerCell.contains(component.description) ) return true;
        }
        return false;
    }

}
