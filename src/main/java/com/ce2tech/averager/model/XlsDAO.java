package com.ce2tech.averager.model;

import lombok.*;
import org.apache.poi.EmptyFileException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public class XlsDAO {

    //FIELDS
    @NonNull private String filePath;

    public TransferObject getData() {
        TransferObject dto = new TransferObject();
        dto.setDataHeader( loadHeaderFromFile() );
        dto.setDataColumns( loadDataFromFile() );
        return dto;
    }

    public void setData(TransferObject dto, String filePath) {
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet();
        Row row;
        Cell cell;

        //File header style settings
        Font font = wb.createFont();
        font.setBold(true);
        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        //File columns style settings
        CellStyle dateStyle = wb.createCellStyle();
        CellStyle timeStyle = wb.createCellStyle();
        CellStyle doubleStyle = wb.createCellStyle();
        CreationHelper creationHelper = wb.getCreationHelper();
        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/mm/yyyy"));
        timeStyle.setDataFormat(creationHelper.createDataFormat().getFormat("hh:mm:ss"));
        doubleStyle.setDataFormat(creationHelper.createDataFormat().getFormat(".00"));


        //Iterators and indexes for columns and rows
        int rowIndex = 0;
        int columnIndex = 0;
        Iterator<String> headerIterator = dto.getDataHeader().iterator();
        Iterator< List<Object> > columnsIterator = dto.getDataColumns().iterator();


        //Create header
        row = sheet.createRow(rowIndex++);
        while (headerIterator.hasNext()) {
            cell = row.createCell(columnIndex++, CellType.STRING);
            cell.setCellValue( headerIterator.next() );
            cell.setCellStyle( headerStyle );
        }


        //Create data columns
        columnIndex = 0;
        while (columnsIterator.hasNext()) {

            for (Object value : columnsIterator.next()) {

                //create new row only for first column
                if ( sheet.getRow(rowIndex) == null ) {
                    row = sheet.createRow(rowIndex++);
                } else {
                    row = sheet.getRow(rowIndex++);
                }

                //Create new cells depend of data type
                cell = row.createCell(columnIndex, CellType.NUMERIC);
                if (value instanceof LocalDate) {
                    Date date = Date.from( ((LocalDate) value).atStartOfDay(ZoneId.systemDefault()).toInstant() );
                    cell.setCellValue(date);
                    cell.setCellStyle(dateStyle);
                } else if (value instanceof LocalTime) {
                    Date time = Date.from( ((LocalTime) value).atDate(LocalDate.of(1970, 1, 1)).atZone(ZoneId.systemDefault()).toInstant() ) ;
                    cell.setCellValue(time);
                    cell.setCellStyle(timeStyle);
                } else if (value instanceof Double) {
                    cell.setCellValue((Double) value);
                    cell.setCellStyle(doubleStyle);
                } else if (value instanceof String) {
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue((String) value);
                }

            }
            rowIndex = 1;
            columnIndex++;
        }

        //Stream workbook to file
        try (FileOutputStream fileOut = new FileOutputStream(filePath) ) {
            wb.write( fileOut );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private List<String> loadHeaderFromFile() {
        List<String> dataHeader = new ArrayList<>();

        try ( NPOIFSFileSystem fs = new NPOIFSFileSystem( new File(filePath) ) ) {
            Workbook wb = new HSSFWorkbook(fs.getRoot(), true);
            Sheet sheet = wb.getSheetAt(0);
            Row row = sheet.getRow(0);

            Iterator<Cell> cellIterator = row.cellIterator();
            while(cellIterator.hasNext()) {
                dataHeader.add( cellIterator.next().getStringCellValue() );
            }

        } catch (IOException | EmptyFileException e) {
            System.out.println(e.getMessage());
        }
        return dataHeader;
    }

    private List< List<Object> > loadDataFromFile()  {
        List< List<Object> > dataColumns = new ArrayList<>();

        try ( NPOIFSFileSystem fs = new NPOIFSFileSystem( new File(filePath) ) ) {
            Workbook wb = new HSSFWorkbook(fs.getRoot(), true);
            Sheet sheet = wb.getSheetAt(0);

            Iterator<Cell> cellIterator = sheet.getRow(0).cellIterator();

            while ( cellIterator.hasNext() ) {
                List<Object> singleColumn = new ArrayList<>();
                dataColumns.add(singleColumn);
                int columnIndex = cellIterator.next().getColumnIndex();

                Iterator<Row> rowIterator = sheet.rowIterator();
                rowIterator.next(); // Skip header

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    switch (columnIndex) {
                        case 0:
                            singleColumn.add(row.getCell(columnIndex).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                            break;
                        case 1:
                            singleColumn.add(row.getCell(columnIndex).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
                            break;
                        default:
                            try {
                                singleColumn.add(row.getCell(columnIndex).getNumericCellValue());
                            } catch (IllegalStateException ise) {
                                singleColumn.add(row.getCell(columnIndex).getStringCellValue());
                            }
                    }
                }

            }
        } catch (IOException | EmptyFileException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return dataColumns;
    }

}