package com.example.stipendi.excel;

import com.example.stipendi.model.CheckInRecord;
import com.example.stipendi.model.Employee;
import com.example.stipendi.model.WorkShift;
import com.example.stipendi.util.ExcelFileUtil;
import com.example.stipendi.util.OvertimeResult;
import com.example.stipendi.util.contract.ErrorHandler;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CheckInExcelReader extends ExcelReader {

    public static List<Employee> readCheckInRecordsFromExcel(String filePath, List<Employee> employees, ErrorHandler errorHandler) {
        List<CheckInRecord> checkInRecords = new ArrayList<>();

        try (Workbook workbook = ExcelFileUtil.getWorkbook(filePath)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0 || isRowEmpty(row)) continue; // Пропускаме заглавния ред и празни редове

                CheckInRecord record = new CheckInRecord();

                String egn = getStringCellValue(row.getCell(1));
                if (egn == null || egn.isEmpty()) {
                    continue; // Пропускаме редове без ЕГН
                }
                record.setEgn(egn);

                try {
                    record.setEntryTime(parseDate(row.getCell(3), errorHandler, row.getRowNum() + 1));
                    record.setExitTime(parseDate(row.getCell(4), errorHandler, row.getRowNum() + 1));
                } catch (Exception e) {
                    errorHandler.addError("Invalid date format at row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                    continue;
                }

                WorkShift workShift = new WorkShift();
                workShift.setType(getStringCellValue(row.getCell(5)));
                record.setWorkShift(workShift);

                record.setRegularHours(parseDuration(row.getCell(6), errorHandler, row.getRowNum() + 1));
                record.setOvertimeHours(parseDuration(row.getCell(7), errorHandler, row.getRowNum() + 1));
                record.setTotalHours(parseDuration(row.getCell(8), errorHandler, row.getRowNum() + 1));

                checkInRecords.add(record);
            }

        } catch (IOException | IllegalArgumentException e) {
            errorHandler.addError(e.getMessage());
        }

        updateEmployeeCheckIn(employees, checkInRecords, errorHandler);

        return employees;
    }

    private static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private static LocalDateTime parseDate(Cell cell, ErrorHandler errorHandler, int rowNum) {
        if (cell == null) {
            errorHandler.addError("Missing date at row " + rowNum);
            throw new IllegalStateException("Missing date");
        }

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getLocalDateTimeCellValue();
                    } else {
                        errorHandler.addError("Invalid numeric date format at row " + rowNum);
                        throw new IllegalStateException("Invalid numeric date format");
                    }
                case STRING:
                    String dateStr = cell.getStringCellValue().trim();
                    DateTimeFormatter formatterWithSeconds = DateTimeFormatter.ofPattern("d.M.yyyy H:mm:ss");
                    DateTimeFormatter formatterWithoutSeconds = DateTimeFormatter.ofPattern("d.M.yyyy H:mm");

                    try {
                        return LocalDateTime.parse(dateStr, formatterWithSeconds);
                    } catch (Exception e) {
                        try {
                            return LocalDateTime.parse(dateStr, formatterWithoutSeconds);
                        } catch (Exception e2) {
                            errorHandler.addError("Invalid date string format at row " + rowNum + ": " + dateStr);
                            throw new IllegalStateException("Invalid date string format: " + dateStr);
                        }
                    }
                default:
                    errorHandler.addError("Unexpected cell type at row " + rowNum + ": " + cell.getCellType());
                    throw new IllegalStateException("Unexpected cell type: " + cell.getCellType());
            }
        } catch (Exception e) {
            errorHandler.addError("Failed to parse date at row " + rowNum + ": " + e.getMessage());
            throw e;
        }
    }

    private static void updateEmployeeCheckIn(List<Employee> employees, List<CheckInRecord> checkInRecords, ErrorHandler errorHandler) {
        for (Employee employee : employees) {
            List<CheckInRecord> employeeRecords = checkInRecords.stream()
                    .filter(record -> record.getEgn().equals(employee.getEgn()))
                    .collect(Collectors.toList());

            int totalOvertimeWeek = 0;
            int totalOvertimeWeekend = 0;
            int totalWorkingDays = calculateTotalWorkdays(employeeRecords);

            for (CheckInRecord record : employeeRecords) {
                if (isSaturday(record.getEntryTime().getDayOfWeek())) {
                    OvertimeResult saturdayResult = calculateOvertimeSaturday(
                            record.getEntryTime(),
                            record.getExitTime(),
                            record.getRegularHours(),
                            employee.getEgn());
                    totalOvertimeWeekend += saturdayResult.getOvertimeHours();
                    if (saturdayResult.hasErrors()) {
                        for (String error : saturdayResult.getErrors()) {
                            errorHandler.addError(error);
                        }
                    }
                } else {
                    OvertimeResult weekResult = calculateOvertimeWeek(
                            record.getEntryTime(),
                            record.getExitTime(),
                            record.getRegularHours(),
                            record.getOvertimeHours(),
                            record.getTotalHours(),
                            record.getWorkShift().getType(),
                            employee.getEgn());
                    totalOvertimeWeek += weekResult.getOvertimeHours();
                    if (weekResult.hasErrors()) {
                        for (String error : weekResult.getErrors()) {
                            errorHandler.addError(error);
                        }
                    }
                }
            }

            employee.setTotalOvertimeWeek(totalOvertimeWeek);
            employee.setTotalOvertimeWeekend(totalOvertimeWeekend);
            employee.setTotalWorkingDays(totalWorkingDays);
        }
    }

    private static OvertimeResult calculateOvertimeSaturday(LocalDateTime startDate, LocalDateTime endDate, double regularHours, String employeeId) {
        OvertimeResult result = new OvertimeResult(0);

        if (!isSaturday(startDate.getDayOfWeek())) {
            result.addError(String.format("ГРЕШКА: Невалиден ден за съботен извънреден труд за служител %s на дата %s",
                    employeeId, startDate.toLocalDate()));
            return result;
        }

        // Проверка на разликата в датите в минути
        long dateDifferenceMinutes = ChronoUnit.MINUTES.between(startDate, endDate);
        if (dateDifferenceMinutes > 24 * 60) { // Повече от 24 часа
            result.addError(String.format("ГРЕШКА ПРИ ЧЕКИРАНЕ ВХОД - ИЗХОД за служител %s на дата %s",
                    employeeId, startDate.toLocalDate()));
            return result;
        }

        if (regularHours >= 0.82) { // 0.82 hours is 49 minutes
            int roundedHours = (int) Math.ceil(Math.min(regularHours, 5.0)); // Limit to 5 hours and round up
            return new OvertimeResult(roundedHours);
        } else {
            return result;
        }
    }

    private static OvertimeResult calculateOvertimeWeek(LocalDateTime startDate, LocalDateTime endDate, double regularHours, double overtimeHours, double totalHours, String workShift, String employeeId) {
        final double MIN_TIME = 8.0; // 8 часа
        final double ROUNDING_THRESHOLD = 49.0 / 60.0; // 49 минути в часове

        OvertimeResult result = new OvertimeResult(0);

        // Проверка на разликата в датите в минути
        long dateDifferenceMinutes = ChronoUnit.MINUTES.between(startDate, endDate);
        if (dateDifferenceMinutes > 24 * 60) { // Повече от 24 часа
            result.addError(String.format("ГРЕШКА ПРИ ЧЕКИРАНЕ ВХОД - ИЗХОД за служител %s на дата %s",
                    employeeId, startDate.toLocalDate()));
            return result;
        }

        // Проверка дали денят е събота
        if (startDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return result;
        }

        //Ако денят от седмицата не е Събота, но работната смяна е Събота и редовното време е под 8 часа, върни 0
        if (!isSaturday(startDate.getDayOfWeek()) && workShift.equals("Събота") && regularHours < MIN_TIME) {
            return result;
        }

        // Ако редовното време е 0, преизчисляваме редовното и извънредното време
        if (regularHours == 0) {
            regularHours = Math.min(MIN_TIME, totalHours);
            overtimeHours = Math.max(0, totalHours - MIN_TIME);
        }

        // Изчисляване на извънредните часове
        if (overtimeHours > ROUNDING_THRESHOLD) {
            return new OvertimeResult(roundHours(overtimeHours));
        } else {
            return result;
        }
    }

    private static int roundHours(double hours) {
        int hourPart = (int) hours;
        double minutePart = (hours - hourPart) * 60;
        if (minutePart >= 49) {
            return hourPart + 1;
        } else {
            return hourPart;
        }
    }

    private static boolean isSaturday(DayOfWeek dayOfWeek) {
        return dayOfWeek == DayOfWeek.SATURDAY;
    }

    private static int calculateTotalWorkdays(List<CheckInRecord> records) {
        return (int) records.stream()
                .filter(record -> record.getEntryTime() != null) // Филтрира празни записи
                .map(record -> record.getEntryTime().toLocalDate())
                .count();
    }

    protected static double parseDuration(Cell cell, ErrorHandler errorHandler, int rowNum) {
        try {
            if (cell == null || cell.getCellType() == CellType.BLANK) {
                return 0;
            }
            if (cell.getCellType() == CellType.STRING) {
                String duration = cell.getStringCellValue();
                if (duration.equals("0")) {
                    return 0;
                }
                String[] parts = duration.split(":");
                if (parts.length != 2 && parts.length != 3) {
                    errorHandler.addError("Invalid duration format at row " + rowNum + ": " + duration);
                    return 0;
                }
                try {
                    double hours = Double.parseDouble(parts[0]);
                    double minutes = Double.parseDouble(parts[1]) / 60;
                    return hours + minutes;
                } catch (NumberFormatException e) {
                    errorHandler.addError("Invalid duration numbers at row " + rowNum + ": " + duration);
                    return 0;
                }
            } else if (cell.getCellType() == CellType.NUMERIC) {
                double duration = cell.getNumericCellValue();
                return duration * 24; // Convert Excel numeric time to hours
            } else {
                errorHandler.addError("Unexpected cell type at row " + rowNum + ": " + cell.getCellType());
                return 0;
            }
        } catch (Exception e) {
            errorHandler.addError("Failed to parse duration at row " + rowNum + ": " + e.getMessage());
            return 0;
        }
    }
}