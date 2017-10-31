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

        dataTable = new JTable(presenter.averageToOneMin(), presenter.getHeaderArray());
        JScrollPane dataTableScroll = new JScrollPane(dataTable);
        add(dataTableScroll);

    }
}
