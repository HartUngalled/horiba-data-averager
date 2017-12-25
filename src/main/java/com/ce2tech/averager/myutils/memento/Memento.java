package com.ce2tech.averager.myutils.memento;

import com.ce2tech.averager.model.dto.TransferObject;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Memento {

    @NonNull @Getter private TransferObject dto;

}