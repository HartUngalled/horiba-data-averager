package com.ce2tech.averager.viewer;

import javax.swing.*;
import java.awt.*;
import java.time.ZoneId;

public class CustomAverage extends JFrame {

    DataPanel dataPanel;

    public CustomAverage(DataPanel dataPanel) {
        this.dataPanel = dataPanel;

        initFrame();
        initContent();

        setVisible(true);
    }


    private void initFrame() {
        setTitle("Custom average");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(200, 150);
        setLocationRelativeTo(dataPanel);
        setLayout(new BorderLayout());
    }

    private void initContent() {
        SpinnerDateModel startModel = new SpinnerDateModel();
        SpinnerDateModel stopModel = new SpinnerDateModel();
        SpinnerNumberModel periodModel = new SpinnerNumberModel();
        JSpinner startSpinner = new JSpinner( startModel );
        JSpinner stopSpinner = new JSpinner( stopModel );
        JSpinner periodSpinner = new JSpinner( periodModel );
        JComponent startEditor = new JSpinner.DateEditor(startSpinner, "HH:mm");
        JComponent stopEditor = new JSpinner.DateEditor(stopSpinner, "HH:mm");
        JComponent periodEditor = new JSpinner.NumberEditor(periodSpinner, "0");
        startSpinner.setEditor(startEditor);
        stopSpinner.setEditor(stopEditor);
        periodSpinner.setEditor(periodEditor);

        JLabel startLabel = new JLabel("From: ");
        JLabel stopLabel = new JLabel("To: ");
        JLabel periodLabel = new JLabel("Period: ");

        JButton calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(
                (e) -> dataPanel.averageData(   periodModel.getNumber().intValue(),
                                                startModel.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime(),
                                                stopModel.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime()) );


        JPanel content = new JPanel(new GridLayout(3, 2));
        content.add(startLabel);
        content.add(startSpinner);
        content.add(stopLabel);
        content.add(stopSpinner);
        content.add(periodLabel);
        content.add(periodSpinner);
        add(content, BorderLayout.CENTER);
        add(calculateButton, BorderLayout.SOUTH);
    }

}
