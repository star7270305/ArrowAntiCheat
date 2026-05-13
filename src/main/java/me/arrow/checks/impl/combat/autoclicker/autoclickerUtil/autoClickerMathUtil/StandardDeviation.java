package me.arrow.checks.impl.combat.autoclicker.autoclickerUtil.autoClickerMathUtil;

import me.arrow.checks.impl.combat.autoclicker.autoclickerUtil.AbstractStatisticFunction;
import me.arrow.checks.impl.combat.autoclicker.autoclickerUtil.StatisticFunction;

public class StandardDeviation extends AbstractStatisticFunction {
    private final StatisticFunction m2 = new Variance();

    public StandardDeviation() {
    }

    public double evaluate(double[] data) {
        return this.sqrt(this.m2.evaluate(data));
    }
}
