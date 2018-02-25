package com.ce2tech.averager.model.dao;

import com.ce2tech.averager.model.dto.Measurand;
import com.ce2tech.averager.model.dto.TransferObject;
import lombok.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

import static com.ce2tech.averager.model.dao.XlsOperations.*;

@RequiredArgsConstructor
public class XlsDAO {

    //FIELDS
    @NonNull private String filePath;
    XlsOperations fileOperator = new XlsOperations();

    public TransferObject getData() {
        List<List<Measurand>> measurement = fileOperator.loadMeasurementFromFile(filePath);

        return new TransferObject(measurement);
    }


    public void setData(TransferObject dto, String filePath) {
        Workbook wb = new HSSFWorkbook();
        wb.createSheet();

        createMeasurementHeaderInWorkbook(wb, dto.getMeasurement());
        createMeasurementInWorkbook(wb, dto.getMeasurement());
        streamToFile(wb, filePath);
    }

}