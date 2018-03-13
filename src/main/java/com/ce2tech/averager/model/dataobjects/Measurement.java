package com.ce2tech.averager.model.dataobjects;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

@NoArgsConstructor
@AllArgsConstructor
public class Measurement implements Iterable {

    private List<Sample> measurement = new ArrayList<>();

    public void add(Sample sample) {
        measurement.add(sample);
    }

    public int size() {
        return measurement.size();
    }

    @Override
    public Iterator<Sample> iterator() {
        return measurement.iterator();
    }

    public Spliterator<Sample> sampleSpliterator() {
        return measurement.spliterator();
    }

    public List<List<Measurand>> getMeasurement() {
        List<List<Measurand>> measurementAsList = new ArrayList<>();

        for (Sample sample : measurement) {
            List<Measurand> sampleAsList = new ArrayList<>();
            for (Measurand measurand : sample) {
                sampleAsList.add(measurand);
            }
            measurementAsList.add(sampleAsList);
        }

        return measurementAsList;
    }

    public void setMeasurement(List<List<Measurand>> measurementAsList) {
        List<Sample> measurement = new ArrayList<>();

        for (List<Measurand> sampleAsList : measurementAsList) {
            Sample sample = new Sample();
            for (Measurand measurand : sampleAsList) {
                sample.add(measurand);
            }
            measurement.add(sample);
        }
        this.measurement = measurement;
    }
}
