package me.arrow.playerdata.processors.impl;

import lombok.Getter;
import lombok.Setter;
import me.arrow.Arrow;
import me.arrow.files.Config;
import me.arrow.managers.profile.Profile;
import me.arrow.playerdata.processors.Processor;
import me.arrow.tasks.TickTask;
import me.arrow.utils.MathUtils;
import me.arrow.utils.TaskUtils;
import me.arrow.utils.custom.CustomLocation;
import me.arrow.utils.custom.SampleList;
import me.arrow.utils.customutils.Math.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;

import static me.arrow.utils.customutils.OtherUtility.*;
import static org.bukkit.Bukkit.getServer;

// idk what is going on in here tbh but i have it use my setback logs

public class SetbackProcessor implements Processor {

    private final SampleList<CustomLocation> locations = new SampleList<>(10, true);

    private final Profile profile;
    private int lastSetbackTicks, lastStoredLocationTicks;


    public HashMap<Player, Boolean> reduce = new HashMap<>();
    @Getter
    @Setter
    public int flags = 0;

    public SetbackProcessor(Profile profile) {
        this.profile = profile;
    }

    @Override
    public void process() {
        if (MathUtils.elapsedTicks(this.lastStoredLocationTicks) < 20
                || MathUtils.elapsedTicks(this.lastSetbackTicks) < 40) return;

        this.locations.add(profile.getMovementData().getLocation());

        this.lastStoredLocationTicks = TickTask.getCurrentTick();
    }

    public void causeSetBack(String callingClass) {
        try {
            Location loc = profile.getMovementData().getLastGroundLocation();
            Location groundBelow = MathUtil.getGroundLocation(profile);

            profile.isExempt().setSetback(true);
            if (loc != null) {
                Bukkit.getScheduler().runTask(Arrow.getInstance().getHost(), () -> profile.getPlayer().teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN));
                setbackDebug(profile, "&c" + callingClass + " &7caused setback at &6" + loc);
                if (Config.Setting.DEBUG.getBoolean()) log(translate("&c" + callingClass + " &7caused setback at &6" + loc));
            } else {
                Bukkit.getScheduler().runTask(Arrow.getInstance().getHost(), () -> profile.getPlayer().teleport(groundBelow, PlayerTeleportEvent.TeleportCause.PLUGIN));
                setbackDebug(profile, "&c" + callingClass + " &7caused setback at &6" + groundBelow);
                if (Config.Setting.DEBUG.getBoolean()) log(translate("&c" + callingClass + " &7caused setback at &6" + groundBelow));
            }


            Bukkit.getScheduler().runTaskLater(Arrow.getInstance().getHost(), () -> profile.isExempt().setSetback(false), 5L);
           // setback(true, callingClass);
        } catch (Exception e) {
            setbackDebug(profile, callingClass + ": Setbacks had an error when attempting to setback to previous location, " + e);
            if (Config.Setting.DEBUG.getBoolean()) log(callingClass + ": Setbacks had an error when attempting to setback to previous location, " + e);
        }
    }

    public void setback(boolean exemptTime, String callingClass) {
        if (exemptTime && MathUtils.elapsedTicks(this.lastSetbackTicks) < 5) return;

        this.lastSetbackTicks = TickTask.getCurrentTick();

        Player p = profile.getPlayer();

        if (p == null) return;

        if (this.locations.isEmpty()) {

            final CustomLocation cloned = profile.getMovementData().getLastLocation().clone();

            int count = 0;

            while (cloned.getBlock().getRelative(BlockFace.DOWN).isEmpty()) {

                cloned.subtract(0D, 1D, 0D);

                //Prevents crashes
                if (count++ > 5) break;
            }

            TaskUtils.task(() -> p.teleport(cloned.toBukkit(), PlayerTeleportEvent.TeleportCause.PLUGIN));

            setbackDebug(profile, "&c"+callingClass + " &7caused setback at &6"+ cloned.toBukkit());

            return;
        }

        final Location setbackLocation = locations.getLast().toBukkit();

        if (setbackLocation.getWorld() != p.getWorld()) return;

        TaskUtils.task(() -> p.teleport(setbackLocation, PlayerTeleportEvent.TeleportCause.PLUGIN));

        setbackDebug(profile, "&c"+callingClass + " &7caused setback at &6"+ setbackLocation);
    }

    public void causeDamageReduction(Profile user){
        reduce.put(user.getPlayer(), true);
        getServer().getScheduler().runTaskLater(Arrow.getInstance().getHost(), () -> reduce.put(user.getPlayer(), false), (60));
    }
}