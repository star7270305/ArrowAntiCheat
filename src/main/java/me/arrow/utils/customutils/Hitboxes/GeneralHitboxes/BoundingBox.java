package me.arrow.utils.customutils.Hitboxes.GeneralHitboxes;

import com.github.retrooper.packetevents.protocol.world.WorldBlockPosition;
import me.arrow.utils.customutils.*;
import me.arrow.utils.customutils.BlockUtils.BlockUtil;
import me.arrow.utils.customutils.BlockUtils.IBlockData;
import me.arrow.utils.customutils.Math.MathUtil;
import me.arrow.utils.minecraft.Vec3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static com.github.retrooper.packetevents.util.MathUtil.clamp;

//Custom bounding box utility, don't remember if it's in Nik's base or i got it from FlopAC, either way, very useful

public class BoundingBox {

    public float minX, minY, minZ, maxX, maxY, maxZ;

    public BoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public BoundingBox(Vector min, Vector max) {
        this.minX = (float) Math.min(min.getX(), max.getX());
        this.minY = (float) Math.min(min.getY(), max.getY());
        this.minZ = (float) Math.min(min.getZ(), max.getZ());
        this.maxX = (float) Math.max(min.getX(), max.getX());
        this.maxY = (float) Math.max(min.getY(), max.getY());
        this.maxZ = (float) Math.max(min.getZ(), max.getZ());
    }

    public BoundingBox add(float x, float y, float z) {
        float newMinX = minX + x;
        float newMaxX = maxX + x;
        float newMinY = minY + y;
        float newMaxY = maxY + y;
        float newMinZ = minZ + z;
        float newMaxZ = maxZ + z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public BoundingBox add(Vector vector) {
        float x = (float) vector.getX(), y = (float) vector.getY(), z = (float) vector.getZ();

        float newMinX = minX + x;
        float newMaxX = maxX + x;
        float newMinY = minY + y;
        float newMaxY = maxY + y;
        float newMinZ = minZ + z;
        float newMaxZ = maxZ + z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public BoundingBox expandMax(double x, double y, double z) {
        this.maxX += (float) x;
        this.maxY += (float) y;
        this.maxZ += (float) z;
        return this;
    }

    public BoundingBox expand(double x, double y, double z) {
        this.minX -= (float) x;
        this.minY -= (float) y;
        this.minZ -= (float) z;
        this.maxX += (float) x;
        this.maxY += (float) y;
        this.maxZ += (float) z;
        return this;
    }

    public BoundingBox addXYZ(double x, double y, double z) {
        this.minX += (float) x;
        this.minY += (float) y;
        this.minZ += (float) z;
        this.maxX += (float) x;
        this.maxY += (float) y;
        this.maxZ += (float) z;
        return this;
    }

    public BoundingBox grow(float x, float y, float z) {
        float newMinX = minX - x;
        float newMaxX = maxX + x;
        float newMinY = minY - y;
        float newMaxY = maxY + y;
        float newMinZ = minZ - z;
        float newMaxZ = maxZ + z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public boolean contains(Vector point) {
        return point.getX() >= minX && point.getX() <= maxX &&
                point.getY() >= minY && point.getY() <= maxY &&
                point.getZ() >= minZ && point.getZ() <= maxZ;
    }

//    public MovingObjectPosition calculateIntercept(Vec3 vecA, Vec3 vecB) {
//        Vec3 vec3 = vecA.getIntermediateWithXValue(vecB, this.minX);
//        Vec3 vec31 = vecA.getIntermediateWithXValue(vecB, this.maxX);
//        Vec3 vec32 = vecA.getIntermediateWithYValue(vecB, this.minY);
//        Vec3 vec33 = vecA.getIntermediateWithYValue(vecB, this.maxY);
//        Vec3 vec34 = vecA.getIntermediateWithZValue(vecB, this.minZ);
//        Vec3 vec35 = vecA.getIntermediateWithZValue(vecB, this.maxZ);
//        if (!this.isVecInYZ(vec3)) {
//            vec3 = null;
//        }
//
//        if (!this.isVecInYZ(vec31)) {
//            vec31 = null;
//        }
//
//        if (!this.isVecInXZ(vec32)) {
//            vec32 = null;
//        }
//
//        if (!this.isVecInXZ(vec33)) {
//            vec33 = null;
//        }
//
//        if (!this.isVecInXY(vec34)) {
//            vec34 = null;
//        }
//
//        if (!this.isVecInXY(vec35)) {
//            vec35 = null;
//        }
//
//        Vec3 vec36 = null;
//        if (vec3 != null) {
//            vec36 = vec3;
//        }
//
//        if (vec31 != null && (vec36 == null || vecA.squareDistanceTo(vec31) < vecA.squareDistanceTo(vec36))) {
//            vec36 = vec31;
//        }
//
//        if (vec32 != null && (vec36 == null || vecA.squareDistanceTo(vec32) < vecA.squareDistanceTo(vec36))) {
//            vec36 = vec32;
//        }
//
//        if (vec33 != null && (vec36 == null || vecA.squareDistanceTo(vec33) < vecA.squareDistanceTo(vec36))) {
//            vec36 = vec33;
//        }
//
//        if (vec34 != null && (vec36 == null || vecA.squareDistanceTo(vec34) < vecA.squareDistanceTo(vec36))) {
//            vec36 = vec34;
//        }
//
//        if (vec35 != null && (vec36 == null || vecA.squareDistanceTo(vec35) < vecA.squareDistanceTo(vec36))) {
//            vec36 = vec35;
//        }
//
//        if (vec36 == null) {
//            return null;
//        } else {
//            EnumFacing enumfacing;
//            if (vec36 == vec3) {
//                enumfacing = EnumFacing.WEST;
//            } else if (vec36 == vec31) {
//                enumfacing = EnumFacing.EAST;
//            } else if (vec36 == vec32) {
//                enumfacing = EnumFacing.DOWN;
//            } else if (vec36 == vec33) {
//                enumfacing = EnumFacing.UP;
//            } else if (vec36 == vec34) {
//                enumfacing = EnumFacing.NORTH;
//            } else {
//                enumfacing = EnumFacing.SOUTH;
//            }
//
//            return new MovingObjectPosition(vec36, enumfacing);
//        }
//    }

    private boolean isVecInYZ(Vec3 vec) {
        return vec != null && vec.yCoord >= this.minY && vec.yCoord <= this.maxY && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    private boolean isVecInXZ(Vec3 vec) {
        return vec != null && vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    private boolean isVecInXY(Vec3 vec) {
        return vec != null && vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.yCoord >= this.minY && vec.yCoord <= this.maxY;
    }

    public boolean isVecInside(Vec3 vec) {
        return vec.xCoord > this.minX && vec.xCoord < this.maxX && vec.yCoord > this.minY && vec.yCoord < this.maxY && vec.zCoord > this.minZ && vec.zCoord < this.maxZ;
    }



    public BoundingBox shrink(float x, float y, float z) {
        float newMinX = minX + x;
        float newMaxX = maxX - x;
        float newMinY = minY + y;
        float newMaxY = maxY - y;
        float newMinZ = minZ + z;
        float newMaxZ = maxZ - z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public BoundingBox add(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new BoundingBox(this.minX + minX, this.minY + minY, this.minZ + minZ, this.maxX + maxX, this.maxY + maxY, this.maxZ + maxZ);
    }

    public BoundingBox subtract(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new BoundingBox(this.minX - minX, this.minY - minY, this.minZ - minZ, this.maxX - maxX, this.maxY - maxY, this.maxZ - maxZ);
    }

    public boolean intersectsWithBox(Vector vector) {
        return (vector.getX() > this.minX && vector.getX() < this.maxX) && ((vector.getY() > this.minY && vector.getY() < this.maxY) && (vector.getZ() > this.minZ && vector.getZ() < this.maxZ));
    }

    public List<CollideEntry> getCollidedBlocks(Player player) {
        //  player.sendMessage("" + player.getLocation().getBlock().getType().name());

        List<CollideEntry> toReturn = new ArrayList<>();
        int minX = MathUtil.floor(this.minX);
        int maxX = MathUtil.floor(this.maxX + 1);
        int minY = MathUtil.floor(this.minY);
        int maxY = MathUtil.floor(this.maxY + 1);
        int minZ = MathUtil.floor(this.minZ);
        int maxZ = MathUtil.floor(this.maxZ + 1);

        for (double x = minX; x < maxX; x++) {
            for (double z = minZ; z < maxZ; z++) {
                for (double y = minY - 1; y < maxY; y++) {
                    toReturn.add(new CollideEntry(BlockUtil.getBlock(new Location(player.getWorld(), x, y, z)),
                            this));
                }
            }
        }

        return toReturn;
    }

    public Vector getMinimum() {
        return new Vector(minX, minY, minZ);
    }

    public Vector getMaximum() {
        return new Vector(maxX, maxY, maxZ);
    }

    public List<Block> getAllBlocks(Player player) {
        Location min = new Location(player.getWorld(), MathUtil.floor(minX), MathUtil.floor(minY), MathUtil.floor(minZ));
        Location max = new Location(player.getWorld(), MathUtil.floor(maxX), MathUtil.floor(maxY), MathUtil.floor(maxZ));
        List<Block> all = new ArrayList<>();
        for (float x = (float) min.getX(); x < max.getX(); x++) {
            for (float y = (float) min.getY(); y < max.getY(); y++) {
                for (float z = (float) min.getZ(); z < max.getZ(); z++) {

                    Block block = BlockUtil.getBlock(new Location(player.getWorld(), x, y, z));

                    assert block != null;
                    if (!block.getType().equals(Material.AIR)) {
                        all.add(block);
                    }
                }
            }
        }
        return all;
    }

    public boolean isInsideBlock(Player player) {
        BoundingBox box = this; // your player's bounding box
        List<CollideEntry> blocks = box.getCollidedBlocks(player);

        for (CollideEntry entry : blocks) {
            Block block = entry.getBlock();
            if (block == null || !block.getType().isSolid()) continue;

            BoundingBox blockBox = new BoundingBox(
                    (float) block.getX(),
                    (float) block.getY(),
                    (float) block.getZ(),
                    (float) block.getX() + 1,
                    (float) block.getY() + 1,
                    (float) block.getZ() + 1
            );

            if (box.intersectsWithBox(blockBox)) {
                return true; // player’s hitbox intersects a solid block
            }
        }

        return false; // no intersections found
    }


    public boolean intersectsWithBox(Object other) {
        if (other == null) return false; // <- safety check

        if (other instanceof BoundingBox otherBox) {
            return otherBox.maxX > this.minX && otherBox.minX < this.maxX
                    && otherBox.maxY > this.minY && otherBox.minY < this.maxY
                    && otherBox.maxZ > this.minZ && otherBox.minZ < this.maxZ;
        }

        // Reflection fallback for legacy AxisAlignedBB
        try {
            double otherMinX = (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "a"), other);
            double otherMinY = (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "b"), other);
            double otherMinZ = (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "c"), other);
            double otherMaxX = (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "d"), other);
            double otherMaxY = (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "e"), other);
            double otherMaxZ = (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "f"), other);

            return otherMaxX > minX && otherMinX < maxX
                    && otherMaxY > minY && otherMinY < maxY
                    && otherMaxZ > minZ && otherMinZ < maxZ;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean collides(Vector vector) {
        return (vector.getX() >= this.minX && vector.getX() <= this.maxX) && ((vector.getY() >= this.minY && vector.getY() <= this.maxY) && (vector.getZ() >= this.minZ && vector.getZ() <= this.maxZ));
    }

    public boolean collides(Object other) {
        if (other instanceof BoundingBox otherBox) {
            return otherBox.maxX >= this.minX && otherBox.minX <= this.maxX && otherBox.maxY >= this.minY && otherBox.minY <= this.maxY && otherBox.maxZ >= this.minZ && otherBox.minZ <= this.maxZ;
        } else {
            float otherMinX = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "a"), other);
            float otherMinY = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "b"), other);
            float otherMinZ = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "c"), other);
            float otherMaxX = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "d"), other);
            float otherMaxY = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "e"), other);
            float otherMaxZ = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "f"), other);
            return otherMaxX >= minX && otherMinX <= maxX && otherMaxY >= minY && otherMinY <= maxY && otherMaxZ >= minZ && otherMinZ <= maxZ;
        }
    }

    public boolean collidesHorizontally(Vector vector) {
        return (vector.getX() >= this.minX && vector.getX() <= this.maxX) && ((vector.getY() > this.minY && vector.getY() < this.maxY) && (vector.getZ() >= this.minZ && vector.getZ() <= this.maxZ));
    }



    public boolean b(BoundingBox var1) {
        if (var1.minX > this.maxX && var1.minX < this.minX) {
            if (var1.minZ > this.maxZ && var1.minZ < this.maxZ) {
                return var1.minY > this.maxY && var1.minY < this.maxY;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean intersectsRay(Vector rayOrigin, Vector rayDirection) {
        double t1 = (minX - rayOrigin.getX()) / rayDirection.getX();
        double t2 = (maxX - rayOrigin.getX()) / rayDirection.getX();
        double t3 = (minY - rayOrigin.getY()) / rayDirection.getY();
        double t4 = (maxY - rayOrigin.getY()) / rayDirection.getY();
        double t5 = (minZ - rayOrigin.getZ()) / rayDirection.getZ();
        double t6 = (maxZ - rayOrigin.getZ()) / rayDirection.getZ();

        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        return !(tmax < 0) && !(tmin > tmax);
    }

    public boolean intersectsRay(Vec3 rayOrigin, Vec3 rayDirection) {
        double t1 = (minX - rayOrigin.xCoord) / rayDirection.xCoord;
        double t2 = (maxX - rayOrigin.xCoord) / rayDirection.xCoord;
        double t3 = (minY - rayOrigin.yCoord) / rayDirection.yCoord;
        double t4 = (maxY - rayOrigin.yCoord) / rayDirection.yCoord;
        double t5 = (minZ - rayOrigin.zCoord) / rayDirection.zCoord;
        double t6 = (maxZ - rayOrigin.zCoord) / rayDirection.zCoord;

        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        if (tmax < 0 || tmin > tmax) {
            return false;
        }

        return true;
    }

    public BoundingBox a(WorldBlockPosition blockposition) {
        return new BoundingBox(blockposition.getBlockPosition().getX() + this.minX,
                blockposition.getBlockPosition().getY() + this.minY, blockposition.getBlockPosition().getZ()
                + this.minZ, blockposition.getBlockPosition().getX() + this.maxX, blockposition.getBlockPosition().getY()
                + this.maxY, blockposition.getBlockPosition().getZ() + this.maxZ);
    }


    public BoundingBox expandWithBlock(WorldBlockPosition blockposition, IBlockData iblockdata) {
        return new BoundingBox(blockposition.getBlockPosition().getX() + this.minX, blockposition.getBlockPosition().getY() + this.minY, blockposition.getBlockPosition().getZ() + this.minZ,
                blockposition.getBlockPosition().getX() + this.maxX, blockposition.getBlockPosition().getY() + this.maxY, blockposition.getBlockPosition().getZ() + this.maxZ);
    }

    public boolean collidesHorizontally(Object other) {
        if (other instanceof BoundingBox otherBox) {
            return otherBox.maxX >= this.minX
                    && otherBox.minX <= this.maxX
                    && otherBox.maxY > this.minY
                    && otherBox.minY < this.maxY
                    && otherBox.maxZ >= this.minZ
                    && otherBox.minZ <= this.maxZ;
        } else {
            float otherMinX = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "a"), other);
            float otherMinY = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "b"), other);
            float otherMinZ = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "c"), other);
            float otherMaxX = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "d"), other);
            float otherMaxY = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "e"), other);
            float otherMaxZ = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "f"), other);
            return otherMaxX >= minX && otherMinX <= maxX && otherMaxY > minY && otherMinY < maxY && otherMaxZ >= minZ && otherMinZ <= maxZ;
        }
    }

    public boolean collidesHorizontally(Object other, double addX, double addY, double addZ) {
        BoundingBox otherBox = (BoundingBox) other;

        return otherBox.maxX >= this.minX && otherBox.minX <= this.maxX
                && otherBox.maxZ >= this.minZ && otherBox.minZ <= this.maxZ;
    }

    public boolean collidesVertically(Vector vector) {
        return (vector.getX() > this.minX && vector.getX() < this.maxX) && ((vector.getY() >= this.minY && vector.getY() <= this.maxY) && (vector.getZ() > this.minZ && vector.getZ() < this.maxZ));
    }

    public boolean collidesVertically(Object other) {
        if (other instanceof BoundingBox) {
            BoundingBox otherBox = (BoundingBox) other;
            return otherBox.maxX > this.minX && otherBox.minX < this.maxX && otherBox.maxY >= this.minY && otherBox.minY <= this.maxY && otherBox.maxZ > this.minZ && otherBox.minZ < this.maxZ;
        } else {
            float otherMinX = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "a"), other);
            float otherMinY = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "b"), other);
            float otherMinZ = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "c"), other);
            float otherMaxX = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "d"), other);
            float otherMaxY = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "e"), other);
            float otherMaxZ = (float) (double) ReflectionUtil.getFieldValue(ReflectionUtil.getFieldByName(other.getClass(), "f"), other);
            return otherMaxX > minX && otherMinX < maxX && otherMaxY >= minY && otherMinY <= maxY && otherMaxZ > minZ && otherMinZ < maxZ;
        }
    }

    public Object toAxisAlignedBB() {
        return ReflectionUtil.newAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }



    public String toString() {
        return "[" + minX + ", " + minY + ", " + minZ + ", " + maxX + ", " + maxY + ", " + maxZ + "]";
    }

    public double distanceToHitbox(BoundingBox box) {
        double cornerX = clamp(this.getCenterX(), box.getCenterX() - 0.4, box.getCenterX() + 0.4);
        double cornerZ = clamp(this.getCenterZ(), box.getCenterZ() - 0.4, box.getCenterZ() + 0.4);
        double distanceX = this.getCenterX() - cornerX;
        double distanceZ = this.getCenterZ() - cornerZ;
        return Math.hypot(distanceX, distanceZ);
    }

    public double getCenterX() {
        return (this.minX + this.maxX) / 2.0;
    }

    public double getCenterY() {
        return (this.minY + this.maxY) / 2.0;
    }

    public double getCenterZ() {
        return (this.minZ + this.maxZ) / 2.0;
    }
}
