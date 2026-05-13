package me.arrow.utils.customutils.raytrace;

import lombok.Getter;

public class RayCastResult {

    @Getter
    private ResultType type;

    private Object object;

    public RayCastResult(ResultType type, Object object) {
        this.type = type;
        this.object = object;
    }

    public Object get() {
        return object;
    }

    public boolean isEmpty() {
        return type == ResultType.EMPTY;
    }

    @Override
    public String toString() {
        return "RayTraceResult{" +
                "type: " + type +
                ", object: " + object +
                '}';
    }
}
