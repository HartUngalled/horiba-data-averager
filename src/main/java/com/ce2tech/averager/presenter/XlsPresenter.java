package com.ce2tech.averager.presenter;

import com.ce2tech.averager.model.dto.Measurand;
import com.ce2tech.averager.model.dto.TransferObject;
import com.ce2tech.averager.model.dao.XlsDAO;
import com.ce2tech.averager.myutils.measurandgetters.MeasurandValueGetter;
import java.time.LocalTime;
import java.util.*;

public class XlsPresenter {

    private XlsDAO dao;
    private TransferObject dto;
    private MeasurandValueGetter measurandGetter = MeasurandValueGetter.getChainOfResponsibility();

    public XlsPresenter(String filePath) {
        dao = new XlsDAO(filePath);
        dto = getData();
    }

    public void setData(String filePatch) {
        dao.setData(dto, filePatch);
    }

    public TransferObject getData() {
        return dao.getData();
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
                measurementAsTable[i][j] = measurandGetter.getValue( measurement.get(i).get(j) );
            }
        }

        return measurementAsTable;
    }

    public void averageToPeriod(int secondsPeriod) {
        List< List<Measurand> > measurement = new ArrayList<>(dto.getMeasurement());
        List< List<Measurand> > averagedMeasurement = new ArrayList<>();

        while(!measurement.isEmpty()) {
            List< List<Measurand> > samplesFromPeriod = getFirstSamplesFromPeriod(measurement, secondsPeriod);
            averagedMeasurement.add(getAveragedSample(samplesFromPeriod));
            measurement.removeAll(samplesFromPeriod);
        }

        dto.setMeasurement(averagedMeasurement);
    }

    public List<Measurand> getAveragedSample(List< List<Measurand> > samplesToAverage) {
        List<Measurand> averagedSample = new ArrayList<>();
        Collections.reverse(samplesToAverage);

        for (List<Measurand> sample : samplesToAverage) {
            //Fill new list with copy of measurands from first sample
            if (averagedSample.isEmpty()) {
                for (Measurand measurand : sample) {
                    averagedSample.add(new Measurand(measurand));
                }
            }
            //Sum only measurands with Double value
            for (Measurand measurand : sample) {
                if (measurandGetter.getValue(measurand) instanceof Double) {
                    int measurandIndex = sample.indexOf(measurand);
                    double sumValue = measurand.getNumericValue() + averagedSample.get(measurandIndex).getNumericValue();
                    averagedSample.set(measurandIndex, new Measurand(measurand.getComponent(), sumValue));
                }
            }

        }
        //Calculate averages from sums
        for (int i=0; i<averagedSample.size(); i++) {
            Measurand measurand = averagedSample.get(i);
            if (measurandGetter.getValue(measurand) instanceof Double) {
                double averagedValue = measurand.getNumericValue()/samplesToAverage.size();
                Measurand replacement = new Measurand(measurand.getComponent(), averagedValue);
                averagedSample.add(i, replacement);
                averagedSample.remove(measurand);
            }
        }
        return averagedSample;
    }

    public List< List<Measurand> > getFirstSamplesFromPeriod(List< List<Measurand> > measurement, int secPeriod){
        List< List<Measurand> > measurementPart = new ArrayList<>();
        LocalTime firstSampleTime;
        LocalTime currentSampleTime;

        for (List<Measurand> sample : measurement) {
            firstSampleTime = measurandGetter.getSampleTime( measurement.iterator().next() );
            currentSampleTime = measurandGetter.getSampleTime(sample);
            if ( currentSampleTime.isAfter(firstSampleTime.plusSeconds(secPeriod-1)) ) {
                return measurementPart;
            } else {
                measurementPart.add(sample);
            }
        }

        return measurementPart;
    }

}