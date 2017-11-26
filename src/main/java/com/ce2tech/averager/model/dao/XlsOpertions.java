package com.ce2tech.averager.model.dao;

import com.ce2tech.averager.model.dto.AcceptableComponents;
import com.ce2tech.averager.model.dto.Measurand;
import com.ce2tech.averager.myutils.measurandgetters.MeasurandValueGetter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;

public class XlsOpertions {

    public static List< List<Measurand> > loadMeasurementFromFile(String filePath)  {
        List< List<Measurand> > measurement = new ArrayList<>();

        //Create virtual workbook from file
        try ( NPOIFSFileSystem fs = new NPOIFSFileSystem( new File(filePath) ) ) {
            Workbook wb = new HSSFWorkbook(fs.getRoot(), true);
            Iterator<Row> rowIterator = wb.sheetIterator().next().rowIterator();

            //Skip header or return empty list when sheet is empty
            if (rowIterator.hasNext()) {
                rowIterator.next();
            } else {
                return measurement;
            }

            //Iterate for every row of file
            while (rowIterator.hasNext()) {
                Iterator<Cell> cellIterator = rowIterator.next().cellIterator();
                List<Measurand> sample = new ArrayList<>();

                //Iterate every cell in row
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String headerCellValue = cell.getSheet().rowIterator().next().getCell(cell.getColumnIndex()).getStringCellValue();
                    //Create measurand only for acceptable components
                    //(in later version GUI will allow to add or remove components)
                    if ( AcceptableComponents.isAcceptableMeasurand( headerCellValue ) ) {
                        Measurand measurand = createMeasurand(headerCellValue, cell);
                        sample.add(measurand);
                    }

                }

                //Throw RuntimeException and break loading data when sample have no time value
                //(for example whole row is string type)
                MeasurandValueGetter.getChainOfResponsibility().getSampleTime(sample);
                measurement.add(sample);

            }

        } catch (IOException | RuntimeException e) {
            System.out.println(e.getMessage());
        }

        return measurement;
    }


    private static Measurand createMeasurand(String headerCellValue, Cell currentCell) {
        Measurand measurand;
        if (currentCell.getCellStyle().getDataFormatString().contains(":")) { //format for time
            measurand = new Measurand(headerCellValue, currentCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
        } else if (currentCell.getCellStyle().getDataFormatString().contains("/")) { //format for date
            measurand = new Measurand(headerCellValue, currentCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        } else if ( currentCell.getCellTypeEnum().equals(CellType.STRING) ) {
            measurand = new Measurand(headerCellValue, currentCell.getStringCellValue());
        } else if ( currentCell.getCellTypeEnum().equals(CellType.NUMERIC) ) {
            measurand = new Measurand(headerCellValue, currentCell.getNumericCellValue());
        } else {
            measurand = new Measurand(headerCellValue, "Can't create value");
        }
        return measurand;
    }


    public static void createMeasurementHeaderInWorkbook(Workbook wb, List< List<Measurand> > measurement) {
        if (wb.getNumberOfSheets() == 0) wb.createSheet();
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
        if (wb.getNumberOfSheets() == 0) wb.createSheet();
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
