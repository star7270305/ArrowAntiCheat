package me.arrow.tasks;

import me.arrow.Arrow;
import me.arrow.checks.types.Check;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A task that we'll be using in order to clear the profile violations.
 */
public class ViolationTask extends BukkitRunnable {

    private final Arrow plugin;

    public ViolationTask(Arrow plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.getProfileManager().getProfileMap().values().forEach(profile -> {
            for (Check check : profile.getCheckHolder().getChecks()) check.resetVl();
        });
    }
}