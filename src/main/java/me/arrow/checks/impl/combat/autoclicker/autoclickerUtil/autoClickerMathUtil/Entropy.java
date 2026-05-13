package me.arrow.checks.impl.combat.autoclicker.autoclickerUtil.autoClickerMathUtil;

import me.arrow.checks.impl.combat.autoclicker.autoclickerUtil.AbstractStatisticFunction;

import java.util.HashMap;
import java.util.Map;

public class Entropy extends AbstractStatisticFunction {
    private static final double LN_2 = Math.log(2.0);

    public Entropy() {
    }

    public double evaluate(double[] data) {
        double n = (double) data.length;
        if (n < 3.0) {
            return Double.NaN;
        } else {
            Map<Double, Integer> valueCounts = new HashMap();
            double[] var5 = data;
            int var6 = data.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                double value = var5[var7];
                valueCounts.put(value, (Integer) valueCounts.computeIfAbsent(value, (k) -> {
                    return 0;
                }) + 1);
            }

            double entropy = valueCounts.values().stream().mapToDouble((freq) -> {
                return (double) freq / n;
            }).map((probability) -> {
                return probability * (Math.log(probability) / LN_2);
            }).sum();
            entropy = -entropy;
            return entropy;
        }
    }
}