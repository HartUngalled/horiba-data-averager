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

@Data
public class XlsDAO {

    //FIELDS
    @NonNull private String filePath;
    private List<String> dataHeader = new ArrayList<>();
    private List< List<Object> > data = new ArrayList<>();

    //METHODS
    public List<String> loadHeaderFromFile() {

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

    public List< List<Object> > loadDataFromFile() {

        try ( NPOIFSFileSystem fs = new NPOIFSFileSystem( new File(filePath) ) ) {
            HSSFWorkbook wb = new HSSFWorkbook(fs.getRoot(), true);
            Sheet sheet = wb.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.rowIterator();
            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                List<Object> dataRow = new ArrayList<>();
                data.add(dataRow);

                int i=0;
                try {
                    dataRow.add(row.getCell(i++).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    dataRow.add(row.getCell(i++).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
                } catch (NullPointerException npe) {
                    break;
                }

                while(true) {
                    try {
                        try {
                            dataRow.add( row.getCell(i).getNumericCellValue() );
                        } catch (IllegalStateException ise) {
                            dataRow.add( row.getCell(i).getStringCellValue());
                        } finally {
                            i++;
                        }
                    } catch (NullPointerException npe) {
                        break;
                    }
                }

            }

            return data;
        } catch (IOException e) {
            return null;
        }

    }

}
