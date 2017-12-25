package com.ce2tech.averager.myutils.memento;

import java.util.ArrayDeque;
import java.util.Deque;

public class Caretaker {

    private Deque<Memento> mementos = new ArrayDeque<>();

    public void addMemento(Memento memento) {
        mementos.addLast(memento);
    }

    public Memento getMemento() {
        return mementos.pollLast();
    }

}