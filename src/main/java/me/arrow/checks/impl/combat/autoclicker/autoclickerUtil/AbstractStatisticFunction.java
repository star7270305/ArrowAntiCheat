package me.arrow.checks.impl.combat.autoclicker.autoclickerUtil;

import lombok.Getter;

@Getter
public abstract class AbstractStatisticFunction implements StatisticFunction, CommonMathFunctions {
    protected boolean biasCorrection = false;

    public AbstractStatisticFunction() {
    }

    public <T extends AbstractStatisticFunction> T correctBias() {
        this.biasCorrection = true;
        return (T) this;
    }

}