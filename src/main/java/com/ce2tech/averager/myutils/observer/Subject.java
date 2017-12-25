package com.ce2tech.averager.myutils.observer;

public interface Subject {

    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(String message);

}
