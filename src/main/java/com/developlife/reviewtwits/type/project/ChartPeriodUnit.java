package com.developlife.reviewtwits.type.project;

import com.querydsl.core.types.dsl.NumberExpression;

import java.time.LocalDateTime;

import static com.developlife.reviewtwits.entity.QStatInfo.statInfo;

public enum ChartPeriodUnit {

    ONE_DAY("1d",LocalDateTime.now().minusDays(1)),
    THREE_DAY("3d",LocalDateTime.now().minusDays(3)),
    FIVE_DAY("5d",LocalDateTime.now().minusDays(5)),
    SEVEN_DAY("7d",LocalDateTime.now().minusDays(7)),
    FIFTEEN_DAY("15d",LocalDateTime.now().minusDays(15)),
    ONE_MONTH("1mo",LocalDateTime.now().minusMonths(1)),
    THREE_MONTH("3mo",LocalDateTime.now().minusMonths(3)),
    SIX_MONTH("6mo",LocalDateTime.now().minusMonths(6)),
    ONE_YEAR("1y",LocalDateTime.now().minusYears(1)),
    THREE_YEAR("3y",LocalDateTime.now().minusYears(3)),
    FIVE_YEAR("5y",LocalDateTime.now().minusYears(5));

    private final String inputValue;
    private final LocalDateTime timeRangeBefore;

    ChartPeriodUnit(String inputValue, LocalDateTime timeRangeBefore) {
        this.inputValue = inputValue;
        this.timeRangeBefore = timeRangeBefore;
    }

    public static boolean checkChartPeriodInput(String input) {
        for (ChartPeriodUnit chartPeriodUnit : ChartPeriodUnit.values()) {
            if (chartPeriodUnit.inputValue.equals(input)) {
                return true;
            }
        }
        return false;
    }

    public static LocalDateTime getTimeRangeBefore(ChartPeriodUnit unit) {
        return unit.timeRangeBefore;
    }

    public static NumberExpression<Integer> getExpressionOfInterval(ChartPeriodUnit interval){
        String dayMonthYear = getDayMonthYear(interval);
        int numberValue = getNumberValue(interval);

        // dayMonthYear, numberValue 중 하나라도 -1 이면 에러를 배출해야 함.

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
