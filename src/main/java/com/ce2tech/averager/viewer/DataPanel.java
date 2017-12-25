package com.ce2tech.averager.viewer;

import com.ce2tech.averager.myutils.observer.Observer;
import com.ce2tech.averager.presenter.XlsPresenter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.time.LocalTime;

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

    public void averageData(int secoundsPeriod, LocalTime start, LocalTime stop) {
        presenter.averageFromStartToStop(secoundsPeriod, start, stop);
        refreshDataTable();
    }

    public void undo() {
        presenter.undo();
        refreshDataTable();
    }

    private void refreshDataTable() {
        dataTable.setModel(createUpToDateModel());
    }

    private TableModel createUpToDateModel() {
        TableModel model = new DefaultTableModel(presenter.getDataToDisplay(), presenter.getHeaderToDisplay());

        if (model.getColumnCount()==0) JOptionPane.showMessageDialog(this, "Data table to show is empty.",
                "No data to display", JOptionPane.WARNING_MESSAGE);

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
