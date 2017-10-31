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
        Object[][] dataArray = getDataArray();
        int rowLength = dataArray[0].length;
        int columnLength = dataArray.length;

        Object[][] oneMinData = new Object[columnLength/6][];
        for (int i=0; i<columnLength/6; i++) {
            oneMinData[i] = new Object[rowLength];
        }


        //Process only file with 10sec sampling time
        if (getDataSamplingTime()!=10) return dataArray;

        for (int j=0; j<rowLength; j++) {

            if (dataColumnIs("Double", j)) {

                double temporarySum = 0;
                for (int i=0; i<columnLength; i++) {
                    temporarySum += (double)dataArray[i][j];
                    if (i%6==5) {
                        oneMinData[i/6][j] = temporarySum/6;
                        temporarySum = 0;
                    }
                }

            } else {

                for (int i=0; i<columnLength/6; i++) {
                    //Take every sixth string from dataArray (last row of average)
                    oneMinData[i][j] = dataArray[(i*6)+5][j];
                }

            }
        }

        return oneMinData;
    }

    //Util for averageToOneMin()
    private boolean dataColumnIs(String dataType, int columnIndex) {
        Object[][] dataArray = getDataArray();
        return dataArray[0][columnIndex].getClass().getSimpleName().equalsIgnoreCase("Double");
    }

    //Util for averageToOneMin()
    private int getDataSamplingTime() {
        Object[][] dataArray = getDataArray();
        int localTimeIndex = getFileLocalTimeIndex();

        LocalTime firstSampleTime = (LocalTime) dataArray[0][localTimeIndex];
        LocalTime secondSampleTime = (LocalTime) dataArray[1][localTimeIndex];

        return (int)firstSampleTime.until(secondSampleTime, SECONDS);
    }

    //Util for averageToOneMin()
    private int getFileLocalTimeIndex() {
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