package me.arrow.nms;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import org.bukkit.util.BoundingBox;
import java.util.ArrayList;
import java.util.List;

public class InstanceDefault implements NmsInstance {

    @Override
    public float getAttackCooldown(Player player) {
        return PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9) ? player.getAttackCooldown() : 1F;
    }


    @Override
    public boolean isChunkLoaded(World world, int x, int z) {
        return world.isChunkLoaded(x >> 4, z >> 4);
    }

    @Override
    public Material getType(Block block) {
        return block.getType();
    }


    @Override
    public Entity[] getChunkEntities(World world, int x, int z) {
        return world.isChunkLoaded(x >> 4, z >> 4) ? world.getChunkAt(x >> 4, z >> 4).getEntities() : new Entity[0];
    }

    @Override
    public boolean isWaterLogged(Block block) {
        return PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)
                && block.getBlockData() instanceof Waterlogged
                && ((Waterlogged) block.getBlockData()).isWaterlogged();
    }

    @Override
    public boolean isCrawling(Player player) {
        return PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)
                && player.getPose() == Pose.SWIMMING;
    }


    @Override
    public boolean isDead(Player player) {
        return player.isDead();
    }

    @Override
    public boolean isSleeping(Player player) {
        return player.isSleeping();
    }

    @Override
    public boolean isSwimming(Player player) {
        return PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13) && player.isSwimming();
    }

//    @Override
//    public boolean isGliding(Player player) {
//        return false;
//    }

    @Override
    public boolean isGliding(Player player) {
        return PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_8_8) && player.isGliding();
    }

    @Override
    public boolean isInsideVehicle(Player player) {
        return player.isInsideVehicle();
    }

    @Override
    public boolean isRiptiding(Player player) {
        return PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13) && player.isRiptiding();
    }

    @Override
    public boolean isBlocking(Player player) {
        return player.isBlocking();
    }

    @Override
    public boolean isSneaking(Player player) {
        return player.isSneaking();
    }

    @Override
    public ItemStack getItemInMainHand(Player player) {
        try {
            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_8_8)) {
                return player.getInventory().getItemInMainHand(); // safe on 1.9+
            } else {
                return player.getItemInHand(); // 1.8
            }
        } catch (NoSuchMethodError e) {
            return player.getItemInHand(); // fallback for 1.8
        }
    }

    @Override
    public ItemStack getItemInOffHand(Player player) {
        try {
            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_8_8)) {
                return player.getInventory().getItemInOffHand(); // safe on 1.9+
            } else {
                return new ItemStack(Material.AIR); // 1.8 has no offhand
            }
        } catch (NoSuchMethodError e) {
            return new ItemStack(Material.AIR); // fallback for 1.8
        }
    }


    @Override
    public float getWalkSpeed(Player player) {
        return player.getWalkSpeed();
    }


    @Override
    public float getAttributeSpeed(Player player) {
        return PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_8_8)  ? (float) player.getAttribute(Attribute.MOVEMENT_SPEED).getValue() : 0.1F;
    }

    @Override
    public boolean getAllowFlight(Player player) {
        return player.getAllowFlight();
    }

    @Override
    public boolean isFlying(Player player) {
        return player.isFlying();
    }

    @Override
    public float getFallDistance(Player player) {
        return player.getFallDistance();
    }

    @Override
    public List<BoundingBox> getCollisionBoxes(World world, int x, int y, int z, WrappedBlockState state) {
        try {
            Block b = world.getBlockAt(x, y, z);
            if (b == null) return null;
            org.bukkit.Material mat = b.getType();
            String name = mat.name().toLowerCase();

            List<BoundingBox> boxes = new ArrayList<>();

            if (mat.isSolid() && !name.contains("slab") && !name.contains("stair") && !name.contains("fence")
                    && !name.contains("wall") && !name.contains("carpet") && !name.contains("pressure_plate")
                    && !name.contains("door") && !name.contains("trapdoor") && !name.contains("sign")) {
                boxes.add(new BoundingBox(x, y, z, x + 1.0, y + 1.0, z + 1.0));
                return boxes;
            }

            if (name.contains("slab")) {
                double slabTop = y + 0.5;
                boxes.add(new BoundingBox(x, y, z, x + 1.0, slabTop, z + 1.0));
                return boxes;
            }

            if (name.contains("stair") || name.contains("stairs")) {
                double stairTop = y + 1.0;
                boxes.add(new BoundingBox(x, y, z, x + 1.0, stairTop, z + 1.0));
                return boxes;
            }

            if (name.contains("carpet")) {
                double h = y + 0.0625;
                boxes.add(new BoundingBox(x, y, z, x + 1.0, h, z + 1.0));
                return boxes;
            }

            if (name.contains("trapdoor") || name.contains("pressure_plate") || name.contains("heavy_pressure_plate")) {
                double h = y + 0.4375;
                boxes.add(new BoundingBox(x, y, z, x + 1.0, h, z + 1.0));
                return boxes;
            }

            if (name.contains("fence") || name.contains("wall")) {
                boxes.add(new BoundingBox(x + 0.25, y, z + 0.25, x + 0.75, y + 1.5, z + 0.75));
                return boxes;
            }

            if (name.contains("door")) {
                boxes.add(new BoundingBox(x, y, z, x + 1.0, y + 1.0, z + 1.0));
                return boxes;
            }

            String sstate = (state == null) ? "" : state.toString().toLowerCase();
            if (sstate.contains("water") || sstate.contains("lava")) {
                double top = y + 0.9;
                try {
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("level=(\\d+)").matcher(sstate);
                    if (m.find()) {
                        int level = Integer.parseInt(m.group(1));
                        double h = Math.max(0.15, 1.0 - (level / 8.0));
                        top = y + h;
                    }
                } catch (Throwable ignored) {}
                boxes.add(new BoundingBox(x, y, z, x + 1.0, top, z + 1.0));
                return boxes;
            }

            if (mat.isSolid()) {
                boxes.add(new BoundingBox(x, y, z, x + 1.0, y + 1.0, z + 1.0));
                return boxes;
            }

        } catch (Throwable ignored) {}

        return null;
    }
}