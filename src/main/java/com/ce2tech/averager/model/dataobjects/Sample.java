package com.ce2tech.averager.model.dataobjects;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Sample implements Iterable<Measurand> {

    @Getter private List<Measurand> sampleAsList = new ArrayList<>();

    public void add(Measurand measurand) {
        sampleAsList.add(measurand);
    }

    @Override
    public Iterator<Measurand> iterator() {
        return sampleAsList.iterator();
    }

}
