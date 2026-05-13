package me.arrow.checks.impl.combat.autoclicker.autoclickerUtil.autoClickerMathUtil;

import me.arrow.checks.impl.combat.autoclicker.autoclickerUtil.AbstractStatisticFunction;

import java.util.Arrays;

public class Min extends AbstractStatisticFunction {
    public Min() {
    }

    public double evaluate(double[] data) {
        return Arrays.stream(data).min().orElse(Double.NaN);
    }
}
