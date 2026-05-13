package me.arrow.utils;

import me.arrow.Arrow;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * A small utility class that we can use in order to create and run tasks quickly
 */
public final class TaskUtils {

    private TaskUtils() {
    }

    public static BukkitTask taskTimer(Runnable runnable, long delay, long interval) {
        return Bukkit.getScheduler().runTaskTimer(Arrow.getInstance().getHost(), runnable, delay, interval);
    }

    public static BukkitTask taskTimerAsync(Runnable runnable, long delay, long interval) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(Arrow.getInstance().getHost(), runnable, delay, interval);
    }

    public static BukkitTask task(Runnable runnable) {
        return Bukkit.getScheduler().runTask(Arrow.getInstance().getHost(), runnable);
    }

    public static BukkitTask taskAsync(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(Arrow.getInstance().getHost(), runnable);
    }

    public static BukkitTask taskLater(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(Arrow.getInstance().getHost(), runnable, delay);
    }

    public static BukkitTask taskLaterAsync(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(Arrow.getInstance().getHost(), runnable, delay);
    }
}