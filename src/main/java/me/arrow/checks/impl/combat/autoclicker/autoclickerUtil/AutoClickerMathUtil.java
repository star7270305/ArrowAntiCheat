package me.arrow.checks.impl.combat.autoclicker.autoclickerUtil;

import java.util.Collection;

public class AutoClickerMathUtil {
    public static double getCps(Collection<? extends Number> samples) {
        return 20 / getAverage(samples);
    }

    public static double[] dequeTranslator(Collection<? extends Number> samples) {
        return samples.stream().mapToDouble(Number::doubleValue).toArray();
    }

    public static double getAverage(Collection<? extends Number> values) {
        return values.stream()
                .mapToDouble(Number::doubleValue)
                .average()
                .orElse(0D);
    }
}
