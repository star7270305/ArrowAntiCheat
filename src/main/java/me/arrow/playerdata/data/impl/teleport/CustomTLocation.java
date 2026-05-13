package me.arrow.playerdata.data.impl.teleport;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

@Setter
public class CustomTLocation implements Cloneable {
    @Getter
    public double x;
    @Getter
    public double y;
    @Getter
    public double z;
    @Getter
    public float yaw;
    @Getter
    public float pitch;
    public boolean ground;
    public boolean cheats;
    public boolean teleport;
    @Getter
    public long timeStamp;

    public CustomTLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timeStamp = System.currentTimeMillis();
    }

    public CustomTLocation(double x, double y, double z, boolean ground) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.ground = ground;
        this.timeStamp = System.currentTimeMillis();
    }

    public CustomTLocation(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.timeStamp = System.currentTimeMillis();
    }

    public CustomTLocation(double x, double y, double z, float yaw, float pitch, long timeStamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.timeStamp = timeStamp;
    }

    public CustomTLocation(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        this.timeStamp = System.currentTimeMillis();
    }

    public CustomTLocation(Vector vector) {
        this.x = vector.getX();
        this.y = vector.getY();
        this.z = vector.getZ();
        this.timeStamp = System.currentTimeMillis();
    }

    public double distance(double x, double y, double z) {
        return Math.abs(this.x - x) + Math.abs(this.y - y) + Math.abs(this.z - z);
    }

    public double distance(com.github.retrooper.packetevents.protocol.world.Location loc) {
        return Math.abs(this.x - loc.getX()) + Math.abs(this.y - loc.getY()) + Math.abs(this.z - loc.getZ());
    }

    public double distance(CustomTLocation o) {
        return Math.sqrt(NumberConversions.square(this.x - o.x) + NumberConversions.square(this.y - o.y) + NumberConversions.square(this.z - o.z));
    }

    public double distance(Location o) {
        return Math.sqrt(NumberConversions.square(this.x - o.getX()) + NumberConversions.square(this.y - o.getY()) + NumberConversions.square(this.z - o.getZ()));
    }

    public double horizontal(CustomTLocation o) {
        return Math.sqrt(NumberConversions.square(this.x - o.x) + NumberConversions.square(this.z - o.z));
    }

    public double horizontal(Location o) {
        return Math.sqrt(NumberConversions.square(this.x - o.getX()) + NumberConversions.square(this.z - o.getZ()));
    }

    public double vertical(Location o) {
        return Math.abs(this.y - o.getY());
    }

    public double vertical(CustomTLocation o) {
        return Math.abs(this.y - o.getY());
    }

    public CustomTLocation clone() throws CloneNotSupportedException {
        return (CustomTLocation)super.clone();
    }

    public Location toLocation(World world) {
        return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public Vector toVector() {
        return new Vector(this.x, this.y, this.z);
    }

    public Vector toBlockVector() {
        return new Vector(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z));
    }

    public int getBlockZ() {
        return NumberConversions.floor(this.z);
    }

    public int getBlockX() {
        return NumberConversions.floor(this.x);
    }

    @Override
    public String toString() {
        return String.format("%.2f", this.x) + ", " + String.format("%.2f", this.y) + ", " + String.format("%.2f", this.z);
    }

    public CustomTLocation subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public void setPosition(double x, double y, double z) {
        this.x = x;
        this.z = z;
        this.y = y;
    }

    public void setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

}