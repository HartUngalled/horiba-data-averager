package com.ce2tech.averager.presenter;

import com.ce2tech.averager.model.XlsDAO;

public class XlsPresenter {

    private XlsDAO dao;

    public XlsPresenter(String filePath) {
        dao = new XlsDAO(filePath);
        dao.loadHeaderFromFile();
        dao.loadDataFromFile();
    }

    public Object[] getHeaderArray() {
        return dao.getDataHeader().toArray();
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

}
