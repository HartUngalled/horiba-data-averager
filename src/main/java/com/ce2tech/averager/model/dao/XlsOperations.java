package com.ce2tech.averager.model.dao;

import com.ce2tech.averager.model.dto.Measurand;
import com.ce2tech.averager.myutils.MeasurandValueGetter;
import org.apache.poi.EmptyFileException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static com.ce2tech.averager.model.dto.AcceptableComponents.isAcceptableMeasurand;
import static com.ce2tech.averager.myutils.DateTimeUtils.convertToLocalDate;
import static com.ce2tech.averager.myutils.DateTimeUtils.convertToLocalTime;

public class XlsOperations {

    public List< List<Measurand> > loadMeasurementFromFile(String filePath)  {
        List< List<Measurand> > measurement = new ArrayList<>();

        try ( NPOIFSFileSystem fs = new NPOIFSFileSystem( new File(filePath) ) ) {
            Workbook workbook = new HSSFWorkbook(fs.getRoot(), true);

            if (isWorkbookEmpty(workbook) || isNotContainHeader(workbook))
                return measurement;

            measurement = createMeasurementFromWorkbook(workbook);

        } catch (IOException | EmptyFileException e) {
            e.printStackTrace();
        }

        return measurement;
    }

    private boolean isWorkbookEmpty(Workbook wb) {
        if (wb.sheetIterator().hasNext())
            if (wb.sheetIterator().next().rowIterator().hasNext())
                return false;
        return true;
    }

    private boolean isNotContainHeader(Workbook wb) {
        Row headerRow = wb.sheetIterator().next().rowIterator().next();
        for (Cell cell : headerRow)
            if (!isStringFormatted(cell))
                return true;
        return false;
    }

    private List<List<Measurand>> createMeasurementFromWorkbook(Workbook workbook) {
        List<List<Measurand>> measurement = new ArrayList<>();

        Iterator<Row> workbookRows = workbook.sheetIterator().next().rowIterator();
        workbookRows.next(); //Skip header

        while (workbookRows.hasNext()) {
            Row row = workbookRows.next();
            List<Measurand> sample = createSampleFromRow(row);
            measurement.add(sample);
        }

        return measurement;
    }

    private List<Measurand> createSampleFromRow(Row row) {
        List<Measurand> sample = new ArrayList<>();

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

    public static void createMeasurementHeaderInWorkbook(Workbook wb, List< List<Measurand> > measurement) {
        if (wb.getNumberOfSheets() == 0) return;
        Sheet sheet = wb.getSheetAt( wb.getActiveSheetIndex() );
        Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());

        //File header style settings
        Font font = wb.createFont();
        font.setBold(true);
        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        //Create header
        measurement.spliterator().tryAdvance(
                (sample) -> {   int columnIndex = 0;
                                for(Measurand measurand : sample) {
                                    Cell cell = row.createCell(columnIndex++, CellType.STRING);
                                    cell.setCellValue( measurand.getComponent() );
                                    cell.setCellStyle( headerStyle );
                                }   });
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
