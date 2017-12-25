package com.ce2tech.averager.myutils.memento;

public interface Originator {

    Memento save();
    void restore(Memento memento);

}