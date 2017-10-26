package com.ce2tech.averager.viewer;

import com.ce2tech.averager.presenter.XlsPresenter;

import javax.swing.*;

public class DataPanel extends JPanel {

    private XlsPresenter presenter = new XlsPresenter("testfile_oneminute.xls");
    private JTable dataTable;

    public DataPanel() {
        init();
    }

    private void init() {

        dataTable = new JTable(presenter.getDataArray(), presenter.getHeaderArray());
        JScrollPane dataTableScroll = new JScrollPane(dataTable);
        add(dataTableScroll);

    }
}
