package com.developlife.reviewtwits.type;

import com.querydsl.core.types.dsl.NumberExpression;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.developlife.reviewtwits.entity.QStatInfo.statInfo;

public enum ChartPeriodUnit {

    ONE_DAY("1d"),
    THREE_DAY("3d"),
    FIVE_DAY("5d"),
    SEVEN_DAY("7d"),
    FIFTEEN_DAY("15d"),
    ONE_MONTH("1mo"),
    THREE_MONTH("3mo"),
    SIX_MONTH("6mo"),
    ONE_YEAR("1y"),
    THREE_YEAR("3y"),
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

    public static LocalDateTime getTimeRangeBefore(LocalDateTime compareDateTime, ChartPeriodUnit range, ChartPeriodUnit interval) {
        String unitOfRange = getPeriodUnit(range);
        String unitOfInterval = getPeriodUnit(interval);
        int numberValue = getNumberValue(range);

        LocalDateTime toReturnDateTime;
        if(unitOfRange.equals("d")){
            toReturnDateTime = compareDateTime.minusDays(numberValue);
        } else if(unitOfRange.equals("mo")){
            toReturnDateTime = compareDateTime.minusMonths(numberValue);
        } else{
            toReturnDateTime = compareDateTime.minusYears(numberValue);
        }

        if(unitOfInterval.equals("d")){
            return toReturnDateTime;
        }else if (unitOfInterval.equals("mo")){
            return toReturnDateTime.withDayOfMonth(1);
        }else{
            return toReturnDateTime.withDayOfMonth(1).withDayOfMonth(1);
        }
    }

    private static int getNumberValue(ChartPeriodUnit interval) {
        String input = interval.inputValue;
        if(input.endsWith("d") || input.endsWith("y")){
            return Integer.parseInt(input.substring(0,input.length()-1));
        }
        if(input.endsWith("mo")){
            return Integer.parseInt(input.substring(0,input.length()-2));
        }
        return -1;
    }

    private static String getPeriodUnit(ChartPeriodUnit unit){
        String input = unit.inputValue;
        if(input.endsWith("d") || input.endsWith("y")){
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

        return null;
    }
}
