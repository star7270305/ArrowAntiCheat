package me.arrow.checks.impl.combat.autoclicker.autoclickerUtil.autoClickerMathUtil;

import me.arrow.checks.impl.combat.autoclicker.autoclickerUtil.AbstractStatisticFunction;

public class Midpoint extends AbstractStatisticFunction {
    public Midpoint() {
    }

    public double evaluate(double[] data) {
        double max = (new Max()).evaluate(data);
        double min = (new Min()).evaluate(data);
        return (max + min) / 2.0;
    }
}
