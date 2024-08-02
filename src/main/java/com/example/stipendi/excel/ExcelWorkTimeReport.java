package com.example.stipendi.excel;

import com.example.stipendi.dao.DirectlyDAO;
import com.example.stipendi.dao.IndirectDAO;
import com.example.stipendi.model.DirectlyOccupied;
import com.example.stipendi.model.IndirectOccupied;
import com.example.stipendi.util.contract.ErrorHandler;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ExcelWorkTimeReport {

    private final DirectlyDAO directlyDAO;
    private final IndirectDAO indirectlyOccupiedDAO;
    private final ErrorHandler errorHandler;

    public ExcelWorkTimeReport(DirectlyDAO directlyDAO, IndirectDAO indirectlyOccupiedDAO, ErrorHandler errorHandler) {
        this.directlyDAO = directlyDAO;
        this.indirectlyOccupiedDAO = indirectlyOccupiedDAO;
        this.errorHandler = errorHandler;
    }

    public void generateWorkHoursReport(String filePath, int repoMonth, int repoYear) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TABLLEA ORE LAVORATE");

        // Create styles
        CellStyle titleStyle = createCellStyle(workbook, new XSSFColor(new byte[]{(byte) 186, (byte) 140, (byte) 220}, null), 16, true, HorizontalAlignment.CENTER);
        CellStyle subtitleDirectStyle = createCellStyle(workbook, new XSSFColor(new byte[]{(byte) 29, (byte) 158, (byte) 255}, null), 11, true, HorizontalAlignment.CENTER);
        CellStyle subtitleIndirectStyle = createCellStyle(workbook, new XSSFColor(new byte[]{(byte) 146, (byte) 208, (byte) 80}, null), 11, true, HorizontalAlignment.CENTER);
        CellStyle headerDirectStyle = createCellStyle(workbook, new XSSFColor(new byte[]{(byte) 29, (byte) 158, (byte) 255}, null), 11, true, HorizontalAlignment.CENTER);
        CellStyle headerIndirectStyle = createCellStyle(workbook, new XSSFColor(new byte[]{(byte) 146, (byte) 208, (byte) 80}, null), 11, true, HorizontalAlignment.CENTER);
        CellStyle hoursStyle = createCellStyle(workbook, null, 11, true, HorizontalAlignment.LEFT);

        Font headerFont = workbook.createFont();
        headerFont.setItalic(true);
        headerFont.setBold(true);
        headerFont.setFontName("Calibri");
        headerFont.setFontHeightInPoints((short) 11);
        headerDirectStyle.setFont(headerFont);
        headerIndirectStyle.setFont(headerFont);

        Font hoursFont = workbook.createFont();
        hoursFont.setUnderline(Font.U_SINGLE);
        hoursFont.setBold(true);
        hoursFont.setFontName("Calibri");
        hoursFont.setFontHeightInPoints((short) 11);
        hoursStyle.setFont(hoursFont);

        int rowIndex = 0;

        // Add title
        Row titleRow = sheet.createRow(rowIndex++);
        createMergedCell(sheet, titleRow, 0, 13, "TABELLA ORE LAVORATE", titleStyle);

        // Add subtitle for Direct
        Row subtitleRow = sheet.createRow(rowIndex++);
        createMergedCell(sheet, subtitleRow, 0, 13, "DIRETTI", subtitleDirectStyle);

        // Get all years
        List<Integer> years = directlyDAO.getAllYears();
        for (Integer year : years) {
            rowIndex = populateData(sheet, directlyDAO.getByYear(year), rowIndex, "TOTALE ORE LAVORATE ", year, headerDirectStyle, hoursStyle);
        }

        // Leave two rows gap
        rowIndex += 2;

        // Add subtitle for Indirect
        subtitleRow = sheet.createRow(rowIndex++);
        createMergedCell(sheet, subtitleRow, 0, 13, "INDIRETTI", subtitleIndirectStyle);

        // Get all years
        for (Integer year : years) {
            rowIndex = populateData(sheet, indirectlyOccupiedDAO.getByYear(year), rowIndex, "TOTALE ORE LAVORATE ", year, headerIndirectStyle, hoursStyle);
        }

        // Adjust column widths
        for (int i = 0; i < 14; i++) {
            sheet.autoSizeColumn(i);
        }

        // Save the workbook
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath + "\\TABELA ORE LAVORATE - " + repoMonth + " - " + repoYear + ".xlsx");
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (IOException e) {
            errorHandler.addError("Error saving file: " + e.getMessage());
        }

        errorHandler.displayErrors();
    }

    private int populateData(Sheet sheet, List<? extends Object> records, int startRow, String totalLabel, int year, CellStyle headerStyle, CellStyle hoursStyle) {
        // Create year row and header row on the same row
        Row yearRow = sheet.createRow(startRow++);
        yearRow.createCell(0).setCellValue("MESE " + year);
        yearRow.getCell(0).setCellStyle(headerStyle);

        List<String> headers = Arrays.asList("GENNAIO", "FEBBRAIO", "MARZO", "APRILE", "MAGGIO", "GIUGNO", "LUGLIO", "AGOSTO", "SETTEMBRE", "OTTOBRE", "NOVEMBRE", "DICEMBRE", "TOTALE ORE");
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = yearRow.createCell(i + 1); // start from column B
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        }

        // Create hours row
        Row hoursRow = sheet.createRow(startRow++);
        hoursRow.createCell(0).setCellValue(totalLabel + year);
        hoursRow.getCell(0).setCellStyle(hoursStyle);

        double[] monthlyHours = new double[12];
        for (Object record : records) {
            if (record instanceof DirectlyOccupied) {
                DirectlyOccupied directRecord = (DirectlyOccupied) record;
                monthlyHours[directRecord.getMonth() - 1] = directRecord.getHours();
            } else if (record instanceof IndirectOccupied) {
                IndirectOccupied indirectRecord = (IndirectOccupied) record;
                monthlyHours[indirectRecord.getMonth() - 1] = indirectRecord.getHours();
            }
        }

        for (int i = 0; i < monthlyHours.length; i++) {
            Cell cell = hoursRow.createCell(i + 1);
            cell.setCellValue(monthlyHours[i]);
            cell.setCellStyle(hoursStyle);
        }

        // Create total hours cell
        Cell totalCell = hoursRow.createCell(13);
        totalCell.setCellFormula("SUM(B" + (hoursRow.getRowNum() + 1) + ":M" + (hoursRow.getRowNum() + 1) + ")");
        totalCell.setCellStyle(hoursStyle);

        return startRow;
    }

    private void createMergedCell(Sheet sheet, Row row, int fromCol, int toCol, String value, CellStyle style) {
        Cell cell = row.createCell(fromCol);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), fromCol, toCol));
    }

    private CellStyle createCellStyle(Workbook workbook, XSSFColor color, int fontSize, boolean isBold, HorizontalAlignment alignment) {
        CellStyle style = workbook.createCellStyle();
        if (color != null) {
            ((XSSFCellStyle) style).setFillForegroundColor(color);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) fontSize);
        font.setBold(isBold);
        style.setFont(font);
        style.setAlignment(alignment);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}