package com.ce2tech.averager.model.dataobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;

@NoArgsConstructor
@AllArgsConstructor
public class Measurement {

    @Getter @Setter private List< List<Measurand> > measurement = new ArrayList<>();

    public void add(Sample sample) {
        measurement.add(sample.getSampleAsList());
    }

    public int size() {
        return measurement.size();
    }

    public Spliterator<List<Measurand>> spliterator() {
        return measurement.spliterator();
    }
}
