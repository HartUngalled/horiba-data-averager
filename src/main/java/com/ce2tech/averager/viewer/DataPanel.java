package com.ce2tech.averager.viewer;

import com.ce2tech.averager.myutils.Observer;
import com.ce2tech.averager.presenter.XlsPresenter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class DataPanel extends JPanel implements Observer {

    private XlsPresenter presenter;
    private JTable dataTable;

    public DataPanel() {
        init();
    }

    private void init() {
        TableModel model = new DefaultTableModel();
        dataTable = new JTable(model);
        JScrollPane dataTableScroll = new JScrollPane(dataTable);
        add(dataTableScroll);
    }

    public void averageData() {
        presenter.averageToOneMin();
        TableModel model = new DefaultTableModel(presenter.getDataArray(), presenter.getHeaderArray());
        dataTable.setModel(model);
    }

    @Override
    public void update(String message) {

        String filePath = message.substring(4);

        if ( message.contains("Save") ) {
            if (!filePath.contains(".xls")) filePath = filePath + ".xls";
            presenter.setData(filePath);
        } else if ( message.contains("Open") ){
            presenter = new XlsPresenter(filePath);
            TableModel model = new DefaultTableModel(presenter.getDataArray(), presenter.getHeaderArray());
            dataTable.setModel(model);
        }
    }
}
