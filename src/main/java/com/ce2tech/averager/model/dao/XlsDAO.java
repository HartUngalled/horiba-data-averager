package com.ce2tech.averager.model.dao;

import com.ce2tech.averager.model.dto.TransferObject;
import lombok.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import static com.ce2tech.averager.model.dao.XlsOpertions.*;

@RequiredArgsConstructor
public class XlsDAO {

    //FIELDS
    @NonNull private String filePath;

    public TransferObject getData() {
        TransferObject dto = new TransferObject();
        dto.setMeasurement( loadMeasurementFromFile(filePath) );
        return dto;
    }


    public void setData(TransferObject dto, String filePath) {
        Workbook wb = new HSSFWorkbook();
        createMeasurementHeaderInWorkbook(wb, dto.getMeasurement());
        createMeasurementInWorkbook(wb, dto.getMeasurement());
        streamToFile(wb, filePath);
    }

}