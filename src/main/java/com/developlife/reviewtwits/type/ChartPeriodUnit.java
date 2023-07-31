package com.developlife.reviewtwits.type;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

public enum ChartPeriodUnit {

    ONE_DAY("1d"),
    ONE_WEEK("1w"),
    ONE_MONTH("1mo"),
    ONE_YEAR("1y"),
    FIVE_YEAR("5y");
    private final String inputValue;

    ChartPeriodUnit(String inputValue) {
        this.inputValue = inputValue;
    }

    public static boolean checkChartPeriodInput(String input) {
        for (ChartPeriodUnit chartPeriodUnit : ChartPeriodUnit.values()) {
            if (chartPeriodUnit.inputValue.equals(input)) {
                return true;
            }
        }
        return false;
    }

    public static ChartPeriodUnit findByInputValue(String input){
        for (ChartPeriodUnit chartPeriodUnit : ChartPeriodUnit.values()) {
            if (chartPeriodUnit.inputValue.equals(input)) {
                return chartPeriodUnit;
            }
        }
        return null;
    }


    public static LocalDateTime getTimeRangeBefore(LocalDateTime compareDateTime, Integer tickCount, ChartPeriodUnit interval){
        String unitOfInterval = getPeriodUnit(interval);
        int numberValue = (getNumberValue(interval) * tickCount) - 1;

        return getLocalDateTime(compareDateTime, unitOfInterval, unitOfInterval, numberValue);
    }

    public static LocalDateTime getTimeRangeBefore(LocalDateTime compareDateTime, ChartPeriodUnit range, ChartPeriodUnit interval) {
        String unitOfRange = getPeriodUnit(range);
        String unitOfInterval = getPeriodUnit(interval);
        int numberValue = getNumberValue(range);

        return getLocalDateTime(compareDateTime, unitOfRange, unitOfInterval, numberValue);
    }

    private static LocalDateTime getLocalDateTime(LocalDateTime compareDateTime, String unitOfRange, String unitOfInterval, int numberValue) {
        LocalDateTime toReturnDateTime = switch (unitOfRange) {
            case "d" -> compareDateTime.minusDays(numberValue);
            case "w" -> compareDateTime.minusWeeks(numberValue);
            case "mo" -> compareDateTime.minusMonths(numberValue);
            default -> compareDateTime.minusYears(numberValue);
        };

        return switch (unitOfInterval) {
            case "d" -> toReturnDateTime;
            case "w" -> toReturnDateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case "mo" -> toReturnDateTime.withDayOfMonth(1);
            default -> toReturnDateTime.withDayOfMonth(1).withDayOfMonth(1);
        };
    }

    private static int getNumberValue(ChartPeriodUnit interval) {
        String input = interval.inputValue;
        if(input.endsWith("d") || input.endsWith("y") || input.endsWith("w")){
            return Integer.parseInt(input.substring(0,input.length()-1));
        }
        if(input.endsWith("mo")){
            return Integer.parseInt(input.substring(0,input.length()-2));
        }
        return -1;
    }

    private static String getPeriodUnit(ChartPeriodUnit unit){
        String input = unit.inputValue;
        if(input.endsWith("d") || input.endsWith("y") || input.endsWith("w")){
            return input.substring(input.length()-1);
        }
        if(input.endsWith("mo")){
            return input.substring(input.length()-2);
        }

        return "nothing";
    }

    public static LocalDate getTimeRangeAfter(LocalDate localDate, ChartPeriodUnit interval) {
        String dayMonthYear = getPeriodUnit(interval);
        int numberValue = getNumberValue(interval);

        if(dayMonthYear.equals("d")){
            return localDate.plusDays(numberValue);
        }
        if(dayMonthYear.equals("mo")){
            return localDate.plusMonths(numberValue);
        }
        if(dayMonthYear.equals("y")){
            return localDate.plusYears(numberValue);
        }
        if(dayMonthYear.equals("w")){
            return localDate.plusWeeks(numberValue);
        }

        return null;
    }
}
