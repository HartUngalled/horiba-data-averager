package com.ce2tech.averager.model.dataacces;

import com.ce2tech.averager.model.dataobjects.Measurand;
import com.ce2tech.averager.model.dataobjects.Measurement;
import com.ce2tech.averager.model.dataobjects.Sample;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

    public Workbook workbook;

    public XlsWriter() {
        prepareEmptyWorkbook();
    }

    public void tryToSaveWorkbookInFile(String filePath) {
        try (FileOutputStream fileOut = new FileOutputStream(filePath) ) {
            workbook.write( fileOut );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public void writeComponentsRowToNextRowOfWorkbook(Measurement measurement) {
        measurement.sampleSpliterator().tryAdvance(
                this::createComponentsRowInWorkbookFromSample);
    }


    private void prepareEmptyWorkbook() {
        workbook = new HSSFWorkbook();
        workbook.createSheet();
    }


    private void createComponentsRowInWorkbookFromSample(Sample sample) {
        CellStyle componentsRowStyle = createStyleForComponentsRow();

        Sheet activeSheet = workbook.getSheetAt( workbook.getActiveSheetIndex() );
        Row lastRowOfSheet = activeSheet.createRow( activeSheet.getPhysicalNumberOfRows() );
        lastRowOfSheet.setRowStyle(componentsRowStyle);

        int columnIndex = 0;
        for(Measurand measurand : sample) {
            Cell cell = lastRowOfSheet.createCell(columnIndex++, CellType.STRING);
            cell.setCellValue( measurand.getComponent() );
        }
    }

    private CellStyle createStyleForComponentsRow() {
        CellStyle componentsRowStyle = workbook.createCellStyle();
        Font font = workbook.createFont();

        font.setBold(true);
        componentsRowStyle.setFont(font);
        componentsRowStyle.setAlignment(HorizontalAlignment.CENTER);

        return componentsRowStyle;
    }

    public void writeMeasurementToWorkbook(List<List<Measurand>> measurement) {
        if (workbook.getNumberOfSheets() == 0) return;
        Sheet sheet = workbook.getSheetAt( workbook.getActiveSheetIndex() );
        Row row;
        Cell cell;

        //File columns style settings
        CellStyle dateStyle = workbook.createCellStyle();
        CellStyle timeStyle = workbook.createCellStyle();
        CellStyle doubleStyle = workbook.createCellStyle();
        CreationHelper creationHelper = workbook.getCreationHelper();
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


}
