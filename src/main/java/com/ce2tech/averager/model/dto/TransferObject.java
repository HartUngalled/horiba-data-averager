package com.ce2tech.averager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class TransferObject {

    @Getter @Setter private List< List<Measurand> > measurement = new ArrayList<>();

}
