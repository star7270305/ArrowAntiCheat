package me.arrow.utils.custom;

import lombok.Getter;


//teleport util, unused
@Getter
public class Teleport {

    private final double x, y, z;
    private final long timeStamp;

    public Teleport(double x, double y, double z, long timeStamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timeStamp = timeStamp;
    }

    public boolean matches(double x, double y, double z) {
        /*
        Check if it's less than .03125D due to precision loss in rare cases
        */
        return Math.abs(this.x - x) < .03125D && Math.abs(this.y - y) < .03125D && Math.abs(this.z - z) < .03125D;
    }

}