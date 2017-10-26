package com.ce2tech.averager.model;

import lombok.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Data
public class XlsDAO {

    //FIELDS
    @NonNull private String filePath;
    private List<String> dataHeader = new ArrayList<>();
    private List< List<Object> > data = new ArrayList<>();

    //METHODS
    public void loadHeaderFromFile() {

        try ( NPOIFSFileSystem fs = new NPOIFSFileSystem( new File(filePath) ) ) {
            HSSFWorkbook wb = new HSSFWorkbook(fs.getRoot(), true);
            Sheet sheet = wb.getSheetAt(0);
            Row row = sheet.getRow(0);

            int i=0;
            while(true) {
                try {
                    dataHeader.add( row.getCell(i++).getStringCellValue() );
                } catch (NullPointerException npe) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDataFromFile() {

        try ( NPOIFSFileSystem fs = new NPOIFSFileSystem( new File(filePath) ) ) {
            HSSFWorkbook wb = new HSSFWorkbook(fs.getRoot(), true);
            Sheet sheet = wb.getSheetAt(0);

            for (int j=1; j<sheet.getPhysicalNumberOfRows(); j++) {
                List<Object> dataRow = new ArrayList<>();
                data.add(dataRow);
                Row row = sheet.getRow(j);

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

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
