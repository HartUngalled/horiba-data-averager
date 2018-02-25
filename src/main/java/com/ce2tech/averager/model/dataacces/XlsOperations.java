package com.ce2tech.averager.model.dataacces;

import com.ce2tech.averager.model.dataobjects.Measurand;
import com.ce2tech.averager.model.dataobjects.Measurement;
import com.ce2tech.averager.model.dataobjects.Sample;
import org.apache.poi.EmptyFileException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.ce2tech.averager.model.AcceptableComponents.isAcceptableMeasurand;
import static com.ce2tech.averager.myutils.DateTimeUtils.convertToLocalDate;
import static com.ce2tech.averager.myutils.DateTimeUtils.convertToLocalTime;

public class XlsOperations {

    public Measurement loadMeasurementFromFile(String filePath)  {
        try ( NPOIFSFileSystem fs = new NPOIFSFileSystem( new File(filePath) ) ) {
            Workbook workbook = new HSSFWorkbook(fs.getRoot(), true);

            if (isWorkbookNotEmpty(workbook) && isWorkbookContainHeader(workbook))
                return createMeasurementFromWorkbook(workbook);

        } catch (IOException | EmptyFileException e) {
            e.printStackTrace();
        }

        return new Measurement();
    }

    private boolean isWorkbookNotEmpty(Workbook wb) {
        if (wb.sheetIterator().hasNext())
            if (wb.sheetIterator().next().rowIterator().hasNext())
                return true;
        return false;
    }

    private boolean isWorkbookContainHeader(Workbook wb) {
        Row headerRow = wb.sheetIterator().next().rowIterator().next();
        for (Cell cell : headerRow)
            if (!isStringFormatted(cell))
                return false;
        return true;
    }

    private Measurement createMeasurementFromWorkbook(Workbook workbook) {
        Measurement measurement = new Measurement();

        Iterator<Row> workbookRows = workbook.sheetIterator().next().rowIterator();
        workbookRows.next(); //Skip header

        while (workbookRows.hasNext()) {
            Row row = workbookRows.next();
            Sample sample = createSampleFromRow(row);
            measurement.add(sample);
        }

        return measurement;
    }

    private Sample createSampleFromRow(Row row) {
        Sample sample = new Sample();

        for (Cell cell : row) {
            String cellHeader = getCellHeader(cell);
            if ( isAcceptableMeasurand( cellHeader ) ) {
                Measurand measurand = createMeasurandFromCell(cellHeader, cell);
                sample.add(measurand);
            }
        }

        return sample;
    }

    private String getCellHeader(Cell cell) {
        Sheet sheet = cell.getSheet();
        Row headerRow = sheet.getRow(0);
        int headerCellIndex = cell.getColumnIndex();

        Cell headerCell = headerRow.getCell(headerCellIndex);
        return headerCell.getStringCellValue();
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



    public static void createMeasurementInWorkbook(Workbook wb, List< List<Measurand> > measurement) {
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


    protected static void streamToFile(Workbook wb, String filePath) {
        try (FileOutputStream fileOut = new FileOutputStream(filePath) ) {
            wb.write( fileOut );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
