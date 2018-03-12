package com.ce2tech.averager.model.dataacces;

import com.ce2tech.averager.model.dataobjects.Measurement;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import static com.ce2tech.averager.model.dataacces.XlsWriter.createMeasurementInWorkbook;
import static com.ce2tech.averager.model.dataacces.XlsWriter.streamToFile;

@RequiredArgsConstructor
public class XlsDAO {

    //FIELDS
    @NonNull
    private String filePath;
    private XlsReader reader = new XlsReader();
    private XlsWriter writer = new XlsWriter();

    public Measurement getData() {
        return reader.loadMeasurementFromFile(filePath);
    }


    public void setData(Measurement dto, String filePath) {
        Workbook wb = new HSSFWorkbook();
        wb.createSheet();

        writer.createMeasurementHeaderInWorkbook(dto, wb);
        createMeasurementInWorkbook(wb, dto.getMeasurement());
        streamToFile(wb, filePath);
    }

}