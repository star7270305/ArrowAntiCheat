package me.arrow.listeners;

import me.arrow.Arrow;
import me.arrow.enums.Permissions;
import me.arrow.files.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * A profile listener that we'll use in order to initialize our player profile.
 */
public class ProfileListener implements Listener {

    private final Arrow plugin;

    public ProfileListener(Arrow plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();

        this.plugin.getProfileManager().createProfile(player);

        if (Config.Setting.TOGGLE_ALERTS_ON_JOIN.getBoolean() && player.hasPermission(Permissions.ALERTS.getPermission())) {

            this.plugin.getAlertManager().addPlayerToAlerts(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent event) {
        this.plugin.getProfileManager().removeProfile(event.getPlayer());
    }
}