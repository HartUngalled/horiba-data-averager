package com.ce2tech.averager.model.dataacces;

import com.ce2tech.averager.model.dataobjects.Measurement;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class XlsDAO {

    //FIELDS
    @NonNull private String filePath;
    private XlsReader fileReader = new XlsReader();
    private XlsWriter fileWriter = new XlsWriter();

    public Measurement getData() {
        return fileReader.readMeasurementFromFile(filePath);
    }

    public void setData(Measurement measurement, String filePath) {
        fileWriter.writeComponentsRowToNextRowOfWorkbook(measurement);
        fileWriter.writeMeasurementToWorkbook(measurement.getMeasurement());
        fileWriter.tryToSaveWorkbookInFile(filePath);
    }

}