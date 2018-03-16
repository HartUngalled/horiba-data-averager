package com.ce2tech.averager.model.config;

import lombok.Getter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

public class WorkbookCellStyle {

    private Workbook workbook;
    private CreationHelper creationHelper;

    @Getter private CellStyle dateStyle;
    @Getter private CellStyle timeStyle;
    @Getter private CellStyle doubleStyle;
    @Getter private CellStyle componentsRowStyle;

    public WorkbookCellStyle(Workbook workbook) {
        this.workbook = workbook;
        creationHelper = workbook.getCreationHelper();

        prepareDateStyle();
        prepareTimeStyle();
        prepareNumericalStyle();
        prepareComponentsRowStyle();
    }

    private void prepareDateStyle() {
        dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/mm/yyyy"));
    }

    private void prepareTimeStyle() {
        timeStyle = workbook.createCellStyle();
        timeStyle.setDataFormat(creationHelper.createDataFormat().getFormat("hh:mm:ss"));
    }

    private void prepareNumericalStyle() {
        doubleStyle = workbook.createCellStyle();
        doubleStyle.setDataFormat(creationHelper.createDataFormat().getFormat(".00"));
    }

    private void prepareComponentsRowStyle() {
        Font font = workbook.createFont();
        componentsRowStyle = workbook.createCellStyle();

        font.setBold(true);
        componentsRowStyle.setFont(font);
        componentsRowStyle.setAlignment(HorizontalAlignment.CENTER);
    }
}
