package com.ce2tech.averager.model;

public class DTO {

    //FIELDS
    XlsDAO dao;

    //CONSTRUCTORS
    public DTO (String filePath) {
        dao = new XlsDAO(filePath);
    }

    //METHODS


    //GETTERS, SETTERS

}
