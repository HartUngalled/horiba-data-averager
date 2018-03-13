package com.ce2tech.averager.model.dataacces;

import com.ce2tech.averager.model.dataobjects.Measurand;
import com.ce2tech.averager.model.dataobjects.Measurement;
import com.ce2tech.averager.model.dataobjects.Sample;
import org.apache.poi.EmptyFileException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static com.ce2tech.averager.model.AcceptableComponents.isAcceptableMeasurand;
import static com.ce2tech.averager.myutils.DateTimeUtils.convertToLocalDate;
import static com.ce2tech.averager.myutils.DateTimeUtils.convertToLocalTime;

public class XlsReader {

    private Workbook workbook;

    public Measurement readMeasurementFromFile(String filePath)  {

        workbook = tryToGetWorkbookFromFile(filePath);

        if (isWorkbookNotEmpty() && isWorkbookDataContainComponentsRow())
            return createMeasurementFromWorkbook();
        else
            return new Measurement();
    }

    private Workbook tryToGetWorkbookFromFile(String filePath) {
        try ( NPOIFSFileSystem fs = new NPOIFSFileSystem( new File(filePath) ) ) {
            return new HSSFWorkbook(fs.getRoot(), true);
        } catch (IOException | EmptyFileException e) {
            return new HSSFWorkbook();
        }
    }

    private boolean isWorkbookNotEmpty() {
        if (workbook.sheetIterator().hasNext())
            if (workbook.sheetIterator().next().rowIterator().hasNext())
                return true;
        return false;
    }

    private boolean isWorkbookDataContainComponentsRow() {
        Row componentsRow = workbook.sheetIterator().next().rowIterator().next();

        for (Cell cell : componentsRow)
            if (!isStringFormatted(cell))
                return false;
        return true;
    }

    private Measurement createMeasurementFromWorkbook() {
        Measurement measurement = new Measurement();
        Iterator<Row> rows = workbook.sheetIterator().next().rowIterator();

        rows.next(); //Skip components row
        while (rows.hasNext()) {
            Row row = rows.next();
            Sample sample = createSampleFromRow(row);
            measurement.add(sample);
        }

        return measurement;
    }

    private Sample createSampleFromRow(Row row) {
        Sample sample = new Sample();

        for (Cell cell : row) {
            String component = getColumnComponent(cell);
            if ( isAcceptableMeasurand( component ) ) {
                Measurand measurand = createMeasurandFromCell(component, cell);
                sample.add(measurand);
            }
        }

        return sample;
    }

    private String getColumnComponent(Cell cell) {
        Sheet sheet = cell.getSheet();
        Row componentsRow = sheet.rowIterator().next();
        int columnIndex = cell.getColumnIndex();

        Cell componentCell = componentsRow.getCell(columnIndex);
        return componentCell.getStringCellValue();
    }

    private Measurand createMeasurandFromCell(String component, Cell cell) {
        Object cellValue = "";

        if (isTimeFormatted(cell))
            cellValue = convertToLocalTime(cell.getDateCellValue());
        else if (isDateFormatted(cell))
            cellValue = convertToLocalDate(cell.getDateCellValue());
        else if (isStringFormatted(cell))
            cellValue = cell.getStringCellValue();
        else if (isNumericFormatted(cell))
            cellValue = cell.getNumericCellValue();

        return new Measurand(component, cellValue);
    }

    private boolean isTimeFormatted(Cell cell) {
        String cellFormat = cell.getCellStyle().getDataFormatString();
        return cellFormat.contains(":");
    }

    private boolean isDateFormatted(Cell cell) {
        String cellFormat = cell.getCellStyle().getDataFormatString();
        return cellFormat.contains("/");
    }

    private boolean isStringFormatted(Cell cell) {
        return cell.getCellTypeEnum().equals(CellType.STRING);
    }

    private boolean isNumericFormatted(Cell cell) {
        return cell.getCellTypeEnum().equals(CellType.NUMERIC);
    }

}
