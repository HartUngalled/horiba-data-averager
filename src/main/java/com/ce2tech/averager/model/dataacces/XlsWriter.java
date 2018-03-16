package com.ce2tech.averager.model.dataacces;

import com.ce2tech.averager.model.dataobjects.Measurand;
import com.ce2tech.averager.model.dataobjects.Measurement;
import com.ce2tech.averager.model.dataobjects.Sample;
import com.ce2tech.averager.myutils.DateTimeUtils;
import com.ce2tech.averager.model.config.WorkbookCellStyle;
import com.ce2tech.averager.myutils.MeasurandValueGetter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class XlsWriter {

    private Workbook workbook;
    private WorkbookCellStyle style;

    public XlsWriter() {
        prepareEmptyWorkbook();
    }

    public Workbook prepareEmptyWorkbook() {
        workbook = new HSSFWorkbook();
        style = new WorkbookCellStyle(workbook);

        workbook.createSheet();
        return workbook;
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

    public void writeMeasurementToWorkbook(Measurement measurement) {
        for (Sample sample : measurement) {
            Row row = createNewRowAtEndOfSheet();
            for (Measurand measurand : sample) {
                createNewCellInRowFromMeasurand(row, measurand);
            }
        }
    }

    private void createComponentsRowInWorkbookFromSample(Sample sample) {
        Row componentsRow = prepareStylizedComponentsRowInWorkbook();

        for(Measurand measurand : sample) {
            Cell componentCell = componentsRow.createCell(componentsRow.getPhysicalNumberOfCells(), CellType.STRING);
            componentCell.setCellValue( measurand.getComponent() );
        }
    }

    private Row prepareStylizedComponentsRowInWorkbook() {
        Row lastRowOfSheet = createNewRowAtEndOfSheet();
        lastRowOfSheet.setRowStyle(style.getComponentsRowStyle());

        return lastRowOfSheet;
    }

    private Row createNewRowAtEndOfSheet() {
        Sheet activeSheet = workbook.getSheetAt( workbook.getActiveSheetIndex() );
        return activeSheet.createRow( activeSheet.getPhysicalNumberOfRows() );
    }


    private void createNewCellInRowFromMeasurand(Row row, Measurand measurand) {
        Cell cell = row.createCell( row.getPhysicalNumberOfCells() );
        Object measurandType = MeasurandValueGetter.getValue(measurand);

        if (measurandType instanceof Double)
            saveMeasurandAsNumericCell(measurand, cell);
        else if ( measurandType instanceof LocalDate )
            saveMeasurandAsDateCell(measurand, cell);
        else if ( measurandType instanceof LocalTime)
            saveMeasurandAsTimeCell(measurand, cell);
        else if ( measurandType instanceof String )
            saveMeasurandAsTextCell(measurand, cell);
    }

    private void saveMeasurandAsNumericCell(Measurand measurand, Cell cell) {
        cell.setCellType(CellType.NUMERIC);
        cell.setCellStyle(style.getDoubleStyle());

        cell.setCellValue( measurand.getNumericValue() );
    }

    private void saveMeasurandAsDateCell(Measurand measurand, Cell cell) {
        cell.setCellType(CellType.NUMERIC);
        cell.setCellStyle(style.getDateStyle());

        Date date = DateTimeUtils.convertToDate( measurand.getDateValue() );
        cell.setCellValue(date);
    }

    private void saveMeasurandAsTimeCell(Measurand measurand, Cell cell) {
        cell.setCellType(CellType.NUMERIC);
        cell.setCellStyle(style.getTimeStyle());

        Date time = DateTimeUtils.convertToDate( measurand.getTimeValue() );
        cell.setCellValue(time);
    }

    private void saveMeasurandAsTextCell(Measurand measurand, Cell cell) {
        cell.setCellType(CellType.STRING);

        cell.setCellValue( measurand.getErrorValue() );
    }

}
