package me.doggy.justguard.utils;

import javafx.util.Pair;

public class MathUtils {

    public static Pair<Double, Double> getMinMax(double a, double b)
    {
        if(a<b)
        {
            return new Pair<>(a,b);
        }
        return new Pair<>(b,a);
    }
    public static boolean isBetweenInclusive(Double a, Pair<Double, Double> range)
    {
        return (a >= range.getKey() && a <= range.getValue());
    }

    public static int tryParseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

}
