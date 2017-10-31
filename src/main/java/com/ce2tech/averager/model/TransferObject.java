package com.ce2tech.averager.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class TransferObject {

    //FIELDS
    @Getter @Setter private List<String> dataHeader = new ArrayList<>();
    @Getter @Setter private List< List<Object> > dataColumns = new ArrayList<>();

}
