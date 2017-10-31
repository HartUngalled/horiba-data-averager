package com.ce2tech.averager.model;

import lombok.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public class XlsDAO {

    //FIELDS
    @NonNull private String filePath;
    private TransferObject dto;

    public TransferObject getData() {
        dto = new TransferObject();
        dto.setDataHeader( loadHeaderFromFile() );
        dto.setDataColumns( loadDataFromFile() );
        return dto;
    }

    //METHODS
    private List<String> loadHeaderFromFile() {
        List<String> dataHeader = new ArrayList<>();

        try ( NPOIFSFileSystem fs = new NPOIFSFileSystem( new File(filePath) ) ) {
            HSSFWorkbook wb = new HSSFWorkbook(fs.getRoot(), true);
            Sheet sheet = wb.getSheetAt(0);
            Row row = sheet.getRow(0);

            Iterator<Cell> cellIterator = row.cellIterator();
            while(cellIterator.hasNext()) {
                dataHeader.add( cellIterator.next().getStringCellValue() );
            }
            return dataHeader;

        } catch (IOException e) {
            return null;
        }
    }

    private List< List<Object> > loadDataFromFile() {
        List< List<Object> > dataColumns = new ArrayList<>();

        try ( NPOIFSFileSystem fs = new NPOIFSFileSystem( new File(filePath) ) ) {
            HSSFWorkbook wb = new HSSFWorkbook(fs.getRoot(), true);
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
            return dataColumns;
        } catch (IOException | NullPointerException e) {
            return null;
        }

    }

}