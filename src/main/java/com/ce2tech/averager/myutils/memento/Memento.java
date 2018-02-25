package com.ce2tech.averager.myutils.memento;

import com.ce2tech.averager.model.dataobjects.Measurement;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Memento {

    @NonNull @Getter private Measurement measurement;

}