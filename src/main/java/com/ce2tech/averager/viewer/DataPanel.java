package com.ce2tech.averager.viewer;

import com.ce2tech.averager.presenter.XlsPresenter;

import javax.swing.*;

public class DataPanel extends JPanel {

    private XlsPresenter presenter = new XlsPresenter("testfile_tensecounds_no.xls");
    private JTable dataTable;

    public DataPanel() {
        init();
    }

    private void init() {

        // uncomment line below to get averaged data
//        presenter.averageToOneMin();
        dataTable = new JTable(presenter.getDataArray(), presenter.getHeaderArray());
        JScrollPane dataTableScroll = new JScrollPane(dataTable);
        add(dataTableScroll);

    }
}
