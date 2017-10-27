package com.ce2tech.averager.presenter;

import com.ce2tech.averager.model.XlsDAO;

import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.SECONDS;

public class XlsPresenter {

    private XlsDAO dao;

    public XlsPresenter(String filePath) {
        dao = new XlsDAO(filePath);
        dao.loadHeaderFromFile();
        dao.loadDataFromFile();
    }

    public String[] getHeaderArray() {
        return dao.getDataHeader().toArray( new String[dao.getDataHeader().size()] );
    }

    public Object[][] getDataArray() {

        int dataRowLength = dao.getDataHeader().size();
        int dataColumnLength = dao.getData().size();

        Object[][] dataArray = new Object[dataColumnLength][dataRowLength];

        for (int i=0; i<dataColumnLength; i++) {
            for (int j=0; j<dataRowLength; j++) {
                dataArray[i][j] = dao.getData().get(i).get(j);
            }
        }

        return dataArray;
    }

    public Object[][] averageToOneMin () {
        Object[][] oneMinData;
        Object[][] dataArray = getDataArray();

        int dataRowLength = dataArray[0].length;
        int dataColumnLength = dataArray.length;

        return dataArray;
    }

    public int getDataSamplingTime() {

        Object[][] dataArray = getDataArray();
        int localTimeIndex = getFileLocalTimeIndex();

        LocalTime firstSampleTime = (LocalTime) dataArray[0][localTimeIndex];
        LocalTime secondSampleTime = (LocalTime) dataArray[1][localTimeIndex];

        return (int)firstSampleTime.until(secondSampleTime, SECONDS);

    }

    public int getFileLocalTimeIndex() {
        Object[][] dataArray = getDataArray();

        int dataRowLength = dataArray[0].length;
        for (int i=0; i<dataRowLength; i++) {
            if ( dataArray[0][i].getClass().getSimpleName().equalsIgnoreCase("LocalTime") ) {
                return i;
            }
        }
        return -1;
    }

}
