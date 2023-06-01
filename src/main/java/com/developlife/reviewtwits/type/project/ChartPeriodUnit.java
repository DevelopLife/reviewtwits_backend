package com.developlife.reviewtwits.type.project;

import com.querydsl.core.types.dsl.NumberExpression;

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

    public static LocalDateTime getTimeRangeBefore(LocalDateTime compareDateTime,ChartPeriodUnit unit) {
        String dayMonthYear = getDayMonthYear(unit);
        int numberValue = getNumberValue(unit);

        if(dayMonthYear.equals("d")){
            return compareDateTime.minusDays(numberValue);
        }
        if(dayMonthYear.equals("mo")){
            return compareDateTime.minusMonths(numberValue);
        }
        if(dayMonthYear.equals("y")){
            return compareDateTime.minusYears(numberValue);
        }

        return null;
    }

    public static NumberExpression<Integer> getExpressionOfInterval(ChartPeriodUnit interval){
        String dayMonthYear = getDayMonthYear(interval);
        int numberValue = getNumberValue(interval);

        if(dayMonthYear.equals("d")){
            return statInfo.createdDate.dayOfYear().divide(numberValue);
        }
        if(dayMonthYear.equals("mo")){
            return statInfo.createdDate.month().divide(numberValue);
        }
        if(dayMonthYear.equals("y")){
            return statInfo.createdDate.year().divide(numberValue);
        }

        return null;
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

    private static String getDayMonthYear(ChartPeriodUnit unit){
        String input = unit.inputValue;
        if(input.endsWith("d") || input.endsWith("y")){
            return input.substring(input.length()-1);
        }
        if(input.endsWith("mo")){
            return input.substring(input.length()-2);
        }

        return "nothing";
    }
}
