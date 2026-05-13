package me.arrow.checks.impl.combat.autoclicker.autoclickerUtil.autoClickerMathUtil;

import me.arrow.checks.impl.combat.autoclicker.autoclickerUtil.AbstractStatisticFunction;

import java.util.Arrays;

public class Max extends AbstractStatisticFunction {
    public Max() {
    }

    public double evaluate(double[] data) {
        return Arrays.stream(data).max().orElse(Double.NaN);
    }
}