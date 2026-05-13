package me.arrow.managers.logs;

import me.arrow.files.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class LogExporter {

    protected static final long DELETE_DAYS = TimeUnit.DAYS.toMillis(Config.Setting.LOGS_CLEAR_DAYS.getInt());

    protected final JavaPlugin plugin;

    public LogExporter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void initialize();

    public abstract void shutdown();

    public abstract void logMultiple(Collection<PlayerLog> logs);

    public abstract void log(PlayerLog log);

    public abstract List<PlayerLog> getLogs();

    public abstract List<PlayerLog> getLogsForPlayer(String player);

    public abstract PagedLogs getLogsForPlayer(String player, int page, int perPage);

    public static class PagedLogs {
        private final List<PlayerLog> logs;
        private final int totalLogs;
        private final int page;
        private final int maxPages;

        public PagedLogs(List<PlayerLog> logs, int totalLogs, int page, int maxPages) {
            this.logs = logs;
            this.totalLogs = totalLogs;
            this.page = page;
            this.maxPages = maxPages;
        }

        public List<PlayerLog> getLogs() {
            return logs;
        }

        public int getTotalLogs() {
            return totalLogs;
        }

        public int getPage() {
            return page;
        }

        public int getMaxPages() {
            return maxPages;
        }
    }
}