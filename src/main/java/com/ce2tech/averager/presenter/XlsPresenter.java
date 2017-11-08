package com.ce2tech.averager.presenter;

import com.ce2tech.averager.model.TransferObject;
import com.ce2tech.averager.model.XlsDAO;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;

public class XlsPresenter {

    private XlsDAO dao;
    private TransferObject dto;

    public XlsPresenter(String filePath) {
        dao = new XlsDAO(filePath);
        getData();
    }

    public TransferObject getData() {
        dto = dao.getData();
        return dto;
    }

    public void setData(String filePath) {
        dao.setData(dto, filePath);
    }

    public String[] getHeaderArray() {
        List<String> headerList = dto.getDataHeader();
        return headerList.toArray( new String[headerList.size()] );
    }

    public Object[][] getDataArray() {
        //Line below is precaution against NoSuchElementException
        int dataColumnLength = dto.getDataColumns().isEmpty() ? 0 : dto.getDataColumns().iterator().next().size();
        int dataRowLength = dto.getDataColumns().size();

        Object[][] dataArray = new Object[dataColumnLength][dataRowLength];

        for (int i=0; i<dataColumnLength; i++) {
            for (int j=0; j<dataRowLength; j++) {
                dataArray[i][j] = dto.getDataColumns().get(j).get(i);
            }
        }

        return dataArray;
    }

    public void averageToOneMin() {
        //Process only file with 10sec sampling time
        if (getDataSamplingTime()!=10) return;

        List<List<Object>> averagedColumns = new ArrayList<>();

        for (List<Object> column : dto.getDataColumns()) {
            List<Object> singleAveragedColumn = new ArrayList<>();
            averagedColumns.add(singleAveragedColumn);

            int i = 0;
            Double tempSum = 0.0;
            for (Object value : column) {
                if (value instanceof Double) {
                    tempSum += (Double) value;
                    if (i%6==5) {
                        singleAveragedColumn.add(tempSum/6);
                        tempSum = 0.0;
                    }
                } else {
                    if (i%6==5) {
                        singleAveragedColumn.add(value);
                    }
                }
                i++;
            }
        }
        dto.setDataColumns( averagedColumns );
    }

    //Util for averageToOneMin()
    private int getDataSamplingTime() {

        for (List<Object> column : dto.getDataColumns()) {
            if (column.get(0) instanceof LocalTime) {

                LocalTime firstSampleTime = (LocalTime) column.get(0);
                LocalTime secondSampleTime = (LocalTime) column.get(1);
                return (int)firstSampleTime.until(secondSampleTime, SECONDS);

            }
        }
        return -1;

    }

}