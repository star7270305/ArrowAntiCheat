package me.arrow.checks.impl.misc.interact;

import me.arrow.managers.profile.Profile;
import me.arrow.utils.customutils.BlockUtils.BlockDataEnum;
import me.arrow.utils.customutils.Hitboxes.CollisionBox;
import me.arrow.utils.customutils.Hitboxes.GeneralHitboxes.SimpleCollisionBox;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Ray / obstruction utilities.
 */
public final class RaytraceUtil {

    private static final double EPS = 1e-8;

    /**
     * Returns the first block that *obstructs* the ray from player's eye to the center of targetBlock.
     * If no obstructing block exists (except the targetBlock itself) returns null.
     *
     * @param user        your Profile object (used to get client/version for getBox)
     * @param player      the player (eye position / world)
     * @param targetBlock the block we intend to break (we allow intersection with this block)
     * @return first obstructing Block or null
     */
    public static Block findObstructingBlock(Profile user, Player player, Block targetBlock) {
        Vector eye = player.getEyeLocation().toVector();
        Vector targetCenter = new Vector(targetBlock.getX() + 0.5d, targetBlock.getY() + 0.5d, targetBlock.getZ() + 0.5d);

        Vector dirVec = targetCenter.clone().subtract(eye);
        double maxDistance = dirVec.length();
        if (maxDistance <= EPS) return null; // degenerate

        dirVec.normalize();

        double ox = eye.getX(), oy = eye.getY(), oz = eye.getZ();
        double dx = dirVec.getX(), dy = dirVec.getY(), dz = dirVec.getZ();

        // Starting voxel (block coordinates)
        int bx = floorToInt(ox);
        int by = floorToInt(oy);
        int bz = floorToInt(oz);

        final int targetX = targetBlock.getX();
        final int targetY = targetBlock.getY();
        final int targetZ = targetBlock.getZ();

        // Steps
        int stepX = dx > 0 ? 1 : (dx < 0 ? -1 : 0);
        int stepY = dy > 0 ? 1 : (dy < 0 ? -1 : 0);
        int stepZ = dz > 0 ? 1 : (dz < 0 ? -1 : 0);

        // tMax = distance along the ray to the first voxel boundary in each axis
        double tMaxX = stepX != 0 ? ((stepX > 0 ? (bx + 1.0 - ox) : (ox - bx)) / Math.abs(dx)) : Double.POSITIVE_INFINITY;
        double tMaxY = stepY != 0 ? ((stepY > 0 ? (by + 1.0 - oy) : (oy - by)) / Math.abs(dy)) : Double.POSITIVE_INFINITY;
        double tMaxZ = stepZ != 0 ? ((stepZ > 0 ? (bz + 1.0 - oz) : (oz - bz)) / Math.abs(dz)) : Double.POSITIVE_INFINITY;

        double tDeltaX = stepX != 0 ? 1.0 / Math.abs(dx) : Double.POSITIVE_INFINITY;
        double tDeltaY = stepY != 0 ? 1.0 / Math.abs(dy) : Double.POSITIVE_INFINITY;
        double tDeltaZ = stepZ != 0 ? 1.0 / Math.abs(dz) : Double.POSITIVE_INFINITY;

        // Player's own block (we usually don't want to consider player's block as obstruction)
        Block playerBlock = player.getLocation().getBlock();

        // Iterate voxels until we reach target block or exceed maxDistance
        int iterations = 0;
        final int MAX_ITERS = 1000; // safety cap
        while (iterations++ < MAX_ITERS) {

            // If we're not at the target block coordinates, test this block as a potential obstruction
            if (!(bx == targetX && by == targetY && bz == targetZ)) {
                // skip player's own block
                if (!(bx == playerBlock.getX() && by == playerBlock.getY() && bz == playerBlock.getZ())) {

                    Block block = player.getWorld().getBlockAt(bx, by, bz);

                    // fetch collision box from your BlockDataEnum (null-check)
                    BlockDataEnum blockData = BlockDataEnum.getData(block.getType());
                    if (blockData != null) {
                        CollisionBox collision = blockData.getBox(block, user.getVersion());
                        if (collision != null && !collision.isNull()) {
                            // downcast into simple boxes and test each as world AABB
                            List<SimpleCollisionBox> simpleBoxes = new ArrayList<>();
                            collision.downCast(simpleBoxes);

                            for (SimpleCollisionBox s : simpleBoxes) {
                                // convert simple (local) box to world coords
                                double minX = bx + s.xMin;
                                double minY = by + s.yMin;
                                double minZ = bz + s.zMin;
                                double maxX = bx + s.xMax;
                                double maxY = by + s.yMax;
                                double maxZ = bz + s.zMax;

                                // fast ray-AABB test (slab method)
                                if (rayIntersectsAABB(ox, oy, oz, dx, dy, dz,
                                        minX, minY, minZ, maxX, maxY, maxZ, maxDistance)) {
                                    // This block obstructs the ray
                                    return block;
                                }
                            }
                        }
                    }
                }
            } else {
                // we've reached the target voxel without finding an obstructing block
                break;
            }

            // Advance to next voxel using the smallest tMax
            if (tMaxX <= tMaxY && tMaxX <= tMaxZ) {
                if (tMaxX > maxDistance) break;
                bx += stepX;
                tMaxX += tDeltaX;
            } else if (tMaxY <= tMaxX && tMaxY <= tMaxZ) {
                if (tMaxY > maxDistance) break;
                by += stepY;
                tMaxY += tDeltaY;
            } else {
                if (tMaxZ > maxDistance) break;
                bz += stepZ;
                tMaxZ += tDeltaZ;
            }
        }

        // no obstruction found
        return null;
    }

    private static int floorToInt(double v) {
        return (int) Math.floor(v);
    }

    /**
     * Ray-AABB intersection using slab method.
     *
     * @return true if ray (origin + t * dir, t >= 0) intersects the AABB within [0, maxDistance]
     */
    private static boolean rayIntersectsAABB(double ox, double oy, double oz,
                                             double dx, double dy, double dz,
                                             double minX, double minY, double minZ,
                                             double maxX, double maxY, double maxZ,
                                             double maxDistance) {
        double tMin = -Double.MAX_VALUE;
        double tMax = Double.MAX_VALUE;

        // X slab
        if (Math.abs(dx) < EPS) {
            if (ox < minX - EPS || ox > maxX + EPS) return false;
        } else {
            double tx1 = (minX - ox) / dx;
            double tx2 = (maxX - ox) / dx;
            double txMin = Math.min(tx1, tx2);
            double txMax = Math.max(tx1, tx2);
            tMin = Math.max(tMin, txMin);
            tMax = Math.min(tMax, txMax);
        }

        // Y slab
        if (Math.abs(dy) < EPS) {
            if (oy < minY - EPS || oy > maxY + EPS) return false;
        } else {
            double ty1 = (minY - oy) / dy;
            double ty2 = (maxY - oy) / dy;
            double tyMin = Math.min(ty1, ty2);
            double tyMax = Math.max(ty1, ty2);
            tMin = Math.max(tMin, tyMin);
            tMax = Math.min(tMax, tyMax);
        }

        // Z slab
        if (Math.abs(dz) < EPS) {
            if (oz < minZ - EPS || oz > maxZ + EPS) return false;
        } else {
            double tz1 = (minZ - oz) / dz;
            double tz2 = (maxZ - oz) / dz;
            double tzMin = Math.min(tz1, tz2);
            double tzMax = Math.max(tz1, tz2);
            tMin = Math.max(tMin, tzMin);
            tMax = Math.min(tMax, tzMax);
        }

        // If intervals overlap and there's intersection in front of origin and within maxDistance
        return tMax >= Math.max(tMin, 0.0) && tMin <= maxDistance + EPS;
    }
}

