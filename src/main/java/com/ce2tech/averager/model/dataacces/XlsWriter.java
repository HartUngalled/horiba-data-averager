package com.ce2tech.averager.model.dataacces;

import com.ce2tech.averager.model.dataobjects.Measurand;
import com.ce2tech.averager.model.dataobjects.Measurement;
import com.ce2tech.averager.model.dataobjects.Sample;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class XlsWriter {


    protected static void streamToFile(Workbook wb, String filePath) {
        try (FileOutputStream fileOut = new FileOutputStream(filePath) ) {
            wb.write( fileOut );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void createMeasurementInWorkbook(Workbook wb, List<List<Measurand>> measurement) {
        if (wb.getNumberOfSheets() == 0) return;
        Sheet sheet = wb.getSheetAt( wb.getActiveSheetIndex() );
        Row row;
        Cell cell;

        //File columns style settings
        CellStyle dateStyle = wb.createCellStyle();
        CellStyle timeStyle = wb.createCellStyle();
        CellStyle doubleStyle = wb.createCellStyle();
        CreationHelper creationHelper = wb.getCreationHelper();
        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/mm/yyyy"));
        timeStyle.setDataFormat(creationHelper.createDataFormat().getFormat("hh:mm:ss"));
        doubleStyle.setDataFormat(creationHelper.createDataFormat().getFormat(".00"));

        //Create data row
        for (List<Measurand> sample : measurement) {
            row = sheet.createRow( sheet.getPhysicalNumberOfRows() );

            //Create new cells depend of data type
            for (Measurand measurand : sample) {

                cell = row.createCell(row.getPhysicalNumberOfCells(), CellType.NUMERIC);
                if ( measurand.getNumericValue() != null ) {
                    cell.setCellValue( measurand.getNumericValue() );
                    cell.setCellStyle(doubleStyle);
                } else if ( measurand.getDateValue() != null ) {
                    Date date = Date.from((measurand.getDateValue()).atStartOfDay(ZoneId.systemDefault()).toInstant());
                    cell.setCellValue(date);
                    cell.setCellStyle(dateStyle);
                } else if ( measurand.getTimeValue() != null ) {
                    Date time = Date.from((measurand.getTimeValue()).atDate(LocalDate.of(1970, 1, 1)).atZone(ZoneId.systemDefault()).toInstant() ) ;
                    cell.setCellValue(time);
                    cell.setCellStyle(timeStyle);
                } else if ( measurand.getErrorValue() != null ) {
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue( measurand.getErrorValue() );
                }
            }
        }
    }


    public void createMeasurementHeaderInWorkbook(Measurement measurement, Workbook workbook) {
        if (isWorkbookContainAnySheets(workbook))
            addHeaderFromMeasurementToNextRowOfWorkbook(measurement, workbook);
    }

    private boolean isWorkbookContainAnySheets(Workbook wb) {
        return wb.getNumberOfSheets() > 0;
    }

    private void addHeaderFromMeasurementToNextRowOfWorkbook(Measurement measurement, Workbook wb) {
        measurement.spliterator().tryAdvance(
                (sample) -> createHeaderInWorkbookFromSample(wb, sample) );
    }

    private void createHeaderInWorkbookFromSample(Workbook wb, Sample sample) {
        CellStyle headerStyle = createStyleForHeader(wb);
        Sheet activeSheet = wb.getSheetAt( wb.getActiveSheetIndex() );
        Row lastRowOfSheet = activeSheet.createRow( activeSheet.getPhysicalNumberOfRows() );

        int columnIndex = 0;
        for(Measurand measurand : sample) {
            Cell cell = lastRowOfSheet.createCell(columnIndex++, CellType.STRING);
            cell.setCellValue( measurand.getComponent() );
            cell.setCellStyle( headerStyle );
        }
    }

    private CellStyle createStyleForHeader(Workbook wb) {
        CellStyle headerStyle = wb.createCellStyle();
        Font font = wb.createFont();

        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        return headerStyle;
    }





}
