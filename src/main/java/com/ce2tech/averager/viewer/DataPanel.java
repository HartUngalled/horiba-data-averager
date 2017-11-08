package com.ce2tech.averager.viewer;

import com.ce2tech.averager.myutils.Observer;
import com.ce2tech.averager.presenter.XlsPresenter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;

public class DataPanel extends JPanel implements Observer {

    private XlsPresenter presenter = new XlsPresenter("");
    private JTable dataTable;

    public DataPanel() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(10,10));

        dataTable = new JTable(new DefaultTableModel());
        JScrollPane dataTableScroll = new JScrollPane(dataTable);
        add(dataTableScroll, BorderLayout.CENTER);
    }

    public void averageData() {
        presenter.averageToOneMin();
        dataTable.setModel(createUpToDateModel());
    }

    private TableModel createUpToDateModel() {
        TableModel model = new DefaultTableModel(presenter.getDataArray(), presenter.getHeaderArray());

        if (model.getColumnCount()==0) JOptionPane.showMessageDialog(this, "You need to open file with aproppiate " +
                "\ndata format to perform calculations.", "Wrong file format", JOptionPane.WARNING_MESSAGE);

        return model;
    }

    @Override
    public void update(String message) {

        String filePath = message.substring(4);

        if ( message.contains("Save") ) {
            filePath += ( filePath.endsWith(".xls") ) ? "" : ".xls";
            presenter.setData(filePath);
        } else if ( message.contains("Open") ){
            presenter = new XlsPresenter(filePath);
            dataTable.setModel( createUpToDateModel() );
        }
    }
}
