package com.ce2tech.averager.viewer;

import com.ce2tech.averager.myutils.Observer;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MainView extends JFrame {


    private List<Observer> observers = new ArrayList<>();
    @Getter private DataPanel dataPanel;

    public MainView() {
        initFrame();
        initDataPanel();
        initMainMenuBar();
        initTestButton();

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

    private void initMainMenuBar() {
        JMenuBar mainMenuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        JMenuItem closeApp = new JMenuItem("Close");
        JMenu options = new JMenu("Options");
        JMenu aboutApp = new JMenu("Help");

        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseFile cf = new ChooseFile(dataPanel, e.getActionCommand());
            }
        });

        saveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseFile cf = new ChooseFile(dataPanel, e.getActionCommand());
            }
        });

        mainMenuBar.add(menuFile);
        menuFile.add(openFile);
        menuFile.add(saveFile);
        menuFile.addSeparator();
        menuFile.add(closeApp);
        mainMenuBar.add(options);
        mainMenuBar.add(aboutApp);

        setJMenuBar(mainMenuBar);
    }

    private void initTestButton() {
        JButton button = new JButton();
        button.setText( "This is a test button. " +
                        "\nClick to average data" +
                        "\nin table on left.");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataPanel.averageData();
            }
        });
        JPanel buttonHolder = new JPanel();
        buttonHolder.add(button);
        add(buttonHolder, BorderLayout.EAST);
    }

}
