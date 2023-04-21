package com.developlife.reviewtwits.type.project;

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
}
