package me.arrow.checks.impl.movement.prediction;

import lombok.Getter;
import me.arrow.utils.custom.MaterialType;
import org.bukkit.Material;
import org.bukkit.World;

// this is really cool, but probably terrible, it was made by GPT, to help detect if you are moving up a block or down a block, it helps fix some issues with my gravity checks
// but it's not perfect, the idea is decent though i think, can be improved

public class MovementPredictionUtil {

    private static final int RADIUS = 2;
    private static final int DEPTH = 2;
    private static final double STEP_THRESHOLD = 0.35D;
    private static final double MOVE_EPS = 1.0E-4D;
    private static final double SIDE_EPS = 0.35D;

    private MovementPredictionUtil() {
    }

    public enum VerticalMove {
        UP,
        DOWN,
        HORIZONTAL
    }

    public enum RelativeMove {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT,
        STATIONARY
    }

    @Getter
    public static final class BlockSample {
        private final int x;
        private final int y;
        private final int z;
        private final Material material;

        public BlockSample(int x, int y, int z, Material material) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.material = material;
        }

    }

    public static BlockSample[] scanAroundFeet(World world, double x, double y, double z) {
        int baseX = floor(x);
        int baseY = floor(y);
        int baseZ = floor(z);

        BlockSample[] samples = new BlockSample[(RADIUS * 2 + 1) * (RADIUS * 2 + 1) * (DEPTH + 1)];
        int index = 0;

        for (int dy = 0; dy >= -DEPTH; dy--) {
            int py = baseY + dy;
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                int px = baseX + dx;
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    int pz = baseZ + dz;
                    Material material = world.getBlockAt(px, py, pz).getType();
                    samples[index++] = new BlockSample(px, py, pz, material);
                }
            }
        }

        return samples;
    }

    public static VerticalMove predictVerticalMove(World world, double x, double y, double z, double deltaX, double deltaZ) {
        if (Math.abs(deltaX) < MOVE_EPS && Math.abs(deltaZ) < MOVE_EPS) {
            return VerticalMove.HORIZONTAL;
        }

        double currentSurface = surfaceScore(world, x, y, z);
        double predictedSurface = surfaceScore(world, x + deltaX, y, z + deltaZ);
        double diff = predictedSurface - currentSurface;

        if (diff >= STEP_THRESHOLD) {
            return VerticalMove.UP;
        }

        if (diff <= -STEP_THRESHOLD) {
            return VerticalMove.DOWN;
        }

        return VerticalMove.HORIZONTAL;
    }

    public static RelativeMove predictRelativeMove(double deltaX, double deltaZ, float yaw) {
        double horizontalLen = Math.hypot(deltaX, deltaZ);
        if (horizontalLen < MOVE_EPS) {
            return RelativeMove.STATIONARY;
        }

        double moveX = deltaX / horizontalLen;
        double moveZ = deltaZ / horizontalLen;

        double rad = Math.toRadians(yaw);
        double lookX = -Math.sin(rad);
        double lookZ = Math.cos(rad);

        double dot = lookX * moveX + lookZ * moveZ;
        double cross = lookX * moveZ - lookZ * moveX;

        if (dot >= SIDE_EPS) {
            return RelativeMove.FORWARD;
        }

        if (dot <= -SIDE_EPS) {
            return RelativeMove.BACKWARD;
        }

        return cross > 0.0D ? RelativeMove.RIGHT : RelativeMove.LEFT;
    }

    public static VerticalMove predictVerticalMove(me.arrow.playerdata.data.impl.MovementData md) {
        return predictVerticalMove(
                md.getLocation().getWorld(),
                md.getLocation().getX(),
                md.getLocation().getY(),
                md.getLocation().getZ(),
                md.getDeltaX(),
                md.getDeltaZ()
        );
    }

    public static RelativeMove predictRelativeMove(me.arrow.playerdata.data.impl.MovementData md,
                                                   me.arrow.playerdata.data.impl.RotationData rd) {
        return predictRelativeMove(md.getDeltaX(), md.getDeltaZ(), rd.getTrustedYaw());
    }

    private static double surfaceScore(World world, double x, double y, double z) {
        int bx = floor(x);
        int by = floor(y);
        int bz = floor(z);

        for (int dy = 0; dy >= -DEPTH; dy--) {
            int py = by + dy;
            if (isBlocking(world, bx, py, bz)) {
                return py + 1.0D;
            }
        }

        return by - DEPTH;
    }

    private static boolean isBlocking(World world, int x, int y, int z) {
        String type = world.getBlockAt(x, y, z).getType().name();

        // Air → not blocking
        if (MaterialType.isMaterial(type, MaterialType.AIR)) return false;

        // Liquids → not blocking (you can enter them)
        if (MaterialType.isMaterial(type, MaterialType.LIQUID)) return false;

        // Plants / transparent → not blocking
        if (MaterialType.isMaterial(type, MaterialType.TRANSPARENT)) return false;

        // Half blocks (carpets, slabs etc.) → depends on your use case
        // If you want strict collision → treat as blocking
        if (MaterialType.isMaterial(type, MaterialType.HALF_BLOCK)) return true;

        // Special non-full but still collidable
        if (MaterialType.isMaterial(type, MaterialType.PANE)) return true;
        if (MaterialType.isMaterial(type, MaterialType.FENCE)) return true;
        if (MaterialType.isMaterial(type, MaterialType.WALL)) return true;
        if (MaterialType.isMaterial(type, MaterialType.DOOR)) return true;
        if (MaterialType.isMaterial(type, MaterialType.TRAPDOOR)) return true;

        // Default: treat everything else as blocking
        return true;
    }

    private static int floor(double value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }
}