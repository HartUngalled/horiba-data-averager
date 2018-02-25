package com.ce2tech.averager.model.dataacces;

import com.ce2tech.averager.model.dataobjects.Measurement;
import lombok.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import static com.ce2tech.averager.model.dataacces.XlsOperations.*;

@RequiredArgsConstructor
public class XlsDAO {

    //FIELDS
    @NonNull private String filePath;
    private XlsOperations fileOperator = new XlsOperations();

    public Measurement getData() {
        return fileOperator.loadMeasurementFromFile(filePath);
    }


    public void setData(Measurement dto, String filePath) {
        Workbook wb = new HSSFWorkbook();
        wb.createSheet();

        createMeasurementHeaderInWorkbook(wb, dto);
        createMeasurementInWorkbook(wb, dto.getMeasurement());
        streamToFile(wb, filePath);
    }

}