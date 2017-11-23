package com.ce2tech.averager.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class TransferObject {

    @Getter @Setter private List< List<Measurand> > measurement = new ArrayList<>();

}
