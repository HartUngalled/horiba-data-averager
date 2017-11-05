package com.ce2tech.averager.viewer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.ce2tech.averager.myutils.Observer;
import com.ce2tech.averager.myutils.Subject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChooseFile extends JFileChooser implements Subject {

    private List<Observer> observers = new ArrayList<>();

    public <T extends Component & Observer> ChooseFile(T parent, String fileOperation) {

        setAcceptAllFileFilterUsed(false);
        setFileFilter(new FileNameExtensionFilter("Old excel format file .xls", "xls"));
        int chosenOption = showDialog(parent, fileOperation);

        if (chosenOption == JFileChooser.APPROVE_OPTION) {
            attach(parent);
            notifyObservers(fileOperation + getSelectedFile());
        }

    }

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
