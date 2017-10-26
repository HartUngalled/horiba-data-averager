package com.ce2tech.averager.viewer;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {

    @Getter private DataPanel dataPanel;

    public MainView() {
        initFrame();
        initDataPanel();

        setVisible(true);
    }

    //METHODS
    private void initFrame() {
        setTitle("Horiba data averager");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLayout(new BorderLayout(10,10));
    }

    private void initDataPanel() {
        dataPanel = new DataPanel();
        add(dataPanel, BorderLayout.WEST);
    }



}
