package me.arrow.checks.impl.combat.autoclicker.autoclickerUtil.autoClickerMathUtil;

import me.arrow.checks.impl.combat.autoclicker.autoclickerUtil.AbstractStatisticFunction;

import java.util.Arrays;

public class Sum extends AbstractStatisticFunction {
    public Sum() {
    }

    public double evaluate(double[] data) {
        return Arrays.stream(data).sum();
    }
}
