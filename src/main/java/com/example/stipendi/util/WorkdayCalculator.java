package com.example.stipendi.util;

import com.example.stipendi.dao.HolidayDAO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;

public class WorkdayCalculator {

    private HolidayDAO holidayDAO;

    public WorkdayCalculator() {
        this.holidayDAO = new HolidayDAO();
    }

    public int getWorkdaysInMonth(int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Месецът трябва да е между 1 и 12");
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate date = yearMonth.atDay(1);
        int workdays = 0;

        while (date.getMonthValue() == month) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workdays++;
            }
            date = date.plusDays(1);
        }

        return workdays - holidayDAO.getHolidayNumByMonth(month);
    }
}