package com.ce2tech.averager.presenter;

import com.ce2tech.averager.model.dataobjects.Measurand;
import com.ce2tech.averager.model.dataobjects.Measurement;
import com.ce2tech.averager.model.dataacces.XlsDAO;
import com.ce2tech.averager.myutils.MeasurandValueGetter;
import com.ce2tech.averager.myutils.memento.Caretaker;
import com.ce2tech.averager.myutils.memento.Memento;

import java.time.LocalTime;
import java.util.*;

public class XlsPresenter {

    private XlsDAO dao;
    private Measurement dto;
    private Caretaker caretaker;

    //CONSTRUCTOR
    public XlsPresenter(String filePath) {
        dao = new XlsDAO(filePath);
        dto = getData();
        caretaker = new Caretaker();
    }

    //METHODS
    public void setData(String filePatch) {
        dao.setData(dto, filePatch);
    }

    public Measurement getData() {
        return dao.getData();
    }

    public void undo() {
        Memento memento = caretaker.getMemento();

        if (memento != null) {
            dto = memento.getMeasurement();
        }
    }

    public String[] getHeaderToDisplay() {
        List<String> header = new ArrayList<>();

        dto.getMeasurement().spliterator().tryAdvance(
                (sample) -> {   for(Measurand measurand : sample)
                                header.add(measurand.getComponent());   } );

        return header.toArray(new String[ header.size() ]);
    }

    public Object[][] getDataToDisplay() {
        List< List<Measurand> > measurement = dto.getMeasurement();
        int columnLength = measurement.size();
        int rowLength = (columnLength>0) ? measurement.iterator().next().size() : 0;

        Object[][] measurementAsTable = new Object[columnLength][rowLength];

        for (int i=0; i<columnLength; i++) {
            for (int j=0; j<rowLength; j++) {
                measurementAsTable[i][j] = MeasurandValueGetter.getValue( measurement.get(i).get(j) );
            }
        }

        return measurementAsTable;
    }



    public List< List<Measurand> > averageFromStartToStop(int secondsPeriod, LocalTime startTime, LocalTime stopTime) {
        List< List<Measurand> > measurement = new ArrayList<>(dto.getMeasurement());
        List< List<Measurand> > averagedMeasurement = new ArrayList<>();
        List< List<Measurand> > samplesFromPeriod;
        List< Measurand > firstSample;

        while(!measurement.isEmpty()) {
            firstSample = measurement.get(0);

            if ( startTime.equals(LocalTime.MIN) ) {
                startTime = MeasurandValueGetter.getSampleTime(firstSample);
            }

            if (    MeasurandValueGetter.getSampleTime(firstSample).isBefore(startTime) ||
                    startTime.isAfter(stopTime.minusSeconds(secondsPeriod))) {
                measurement.remove(firstSample);
            }
            else {
                samplesFromPeriod = getFirstSamplesFromPeriod(measurement, secondsPeriod, startTime);
                measurement.removeAll(samplesFromPeriod);

                if (!samplesFromPeriod.isEmpty())
                    averagedMeasurement.add( getAveragedSample(samplesFromPeriod) );

                startTime = startTime.plusSeconds( secondsPeriod );
            }
        }

        caretaker.addMemento( new Memento( dtoDeepCopy(dto) ) );
        dto.setMeasurement(averagedMeasurement);
        return averagedMeasurement;
    }


    private List< List<Measurand> > getFirstSamplesFromPeriod(List< List<Measurand> > measurement, int secPeriod, LocalTime beginningTime){
        List< List<Measurand> > measurementPart = new ArrayList<>();
        LocalTime currentSampleTime;

        for (List<Measurand> sample : measurement) {
            currentSampleTime = MeasurandValueGetter.getSampleTime(sample);
            if ( !currentSampleTime.isBefore(beginningTime.plusSeconds(secPeriod)) ) {
                return measurementPart;
            } else {
                measurementPart.add(sample);
            }
        }

        return measurementPart;
    }


    private List<Measurand> getAveragedSample(List< List<Measurand> > samplesToAverage) {
        List<Measurand> averagedSample = new ArrayList<>();
        Collections.reverse(samplesToAverage);

        for (List<Measurand> sample : samplesToAverage) {
            //Fill new list with copy of measurands from first sample
            if (averagedSample.isEmpty()) {
                for (Measurand measurand : sample) {
                    averagedSample.add(new Measurand(measurand));
                }
            } else {
                //Sum only measurands with Double value
                for (Measurand measurand : sample) {
                    if (MeasurandValueGetter.getValue(measurand) instanceof Double) {
                        int measurandIndex = sample.indexOf(measurand);
                        double sumValue = measurand.getNumericValue() + averagedSample.get(measurandIndex).getNumericValue();
                        averagedSample.set(measurandIndex, new Measurand(measurand.getComponent(), sumValue));
                    }
                }
            }
        }

        //Calculate averages from sums
        for (int i=0; i<averagedSample.size(); i++) {
            Measurand measurand = averagedSample.get(i);
            if (MeasurandValueGetter.getValue(measurand) instanceof Double) {
                double averagedValue = measurand.getNumericValue() / (samplesToAverage.size());
                Measurand replacement = new Measurand(measurand.getComponent(), averagedValue);
                averagedSample.add(i, replacement);
                averagedSample.remove(measurand);
            }
        }
        return averagedSample;
    }

    private Measurement dtoDeepCopy(Measurement originalDto) {
        List< List<Measurand> > originalMeasurement = originalDto.getMeasurement();
        List< List<Measurand> > copiedMeasurement = new ArrayList<>();

        for (List<Measurand> originalSample : originalMeasurement) {
            List<Measurand> copiedSample = new ArrayList<>();
            for (Measurand originalMeasurand : originalSample) {
                Measurand copiedMeasurand = new Measurand(originalMeasurand);
                copiedSample.add( copiedMeasurand );
            }
            copiedMeasurement.add(copiedSample);
        }

        Measurement copiedDto = new Measurement();
        copiedDto.setMeasurement(copiedMeasurement);
        return copiedDto;
    }
}