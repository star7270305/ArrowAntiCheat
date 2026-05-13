package me.arrow.listeners;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import me.arrow.Arrow;
import me.arrow.managers.profile.Profile;
import me.arrow.playerdata.data.Data;
import me.arrow.utils.ChatUtils;
import me.arrow.utils.TaskUtils;
import me.arrow.utils.custom.ExpiringSet;
import me.arrow.utils.versionutils.VersionUtils;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

// does not seem to work properly on modern purpur server, idk if its a paper issue, or modern mc issue, but it returns vanilla on almost all clients. works fine on 1.8.

/**
 * A client listener that we'll use in order to get the profile's client brand.
 */
public class ClientBrandListener implements Data {

    private final Arrow plugin;

    /*
    We need to do this in order to fix edge cases that mostly occur in bungeecord servers
    Where the client brand payload would get sent more than once.
     */
    private final ExpiringSet<UUID> cache = new ExpiringSet<>(5000L);

    public ClientBrandListener(Arrow plugin) {
        this.plugin = plugin;
    }

    @Override
    public void processReceive(PacketReceiveEvent event) {
        if (event.getPlayer() == null) return;

        if (!event.getPacketType().equals(PacketType.Play.Client.PLUGIN_MESSAGE)) return;

        Player player = event.getPlayer();

        UUID uuid = player.getUniqueId();

        WrapperPlayClientPluginMessage payload = new WrapperPlayClientPluginMessage(event);

        String channel = payload.getChannelName();

        /*
        Check if we received a payload from the brand channel
        Or if the player has set his brand recently.
         */
        if (channel == null
                || !channel.toLowerCase().endsWith("brand")
                || this.cache.contains(uuid)) return;

        String brand;

        try {

            /*
            Clear any color codes to make sure they're not exploiting this
            And translate the bytes.
             */
            byte[] data = payload.getData();
            if (data.length == 0) return;

            brand = ChatUtils.stripColorCodes(new String(data, StandardCharsets.UTF_8));

        } catch (Exception ex) {

            /*
            Cant parse, should never happen unless a client is doing it intentionally.
             */
            return;
        }

        /*
        Add the player's uuid to the cache
         */
        this.cache.add(uuid);

        /*
        Schedule it to run two seconds later to make sure the player profile has been initialized
         */
        TaskUtils.taskLaterAsync(() -> {

            Profile profile = this.plugin.getProfileManager().getProfile(player);

            /*
            Just to make sure.
             */
            if (profile == null) return;

            String clientBrand = brand;

            if (clientBrand.equalsIgnoreCase("Cave Client")) {
                clientBrand = "Cave Client";
            } else if (clientBrand.contains("lunarclient")) {
                clientBrand = "Lunar Client";
            } else if (clientBrand.contains("PLC18")) {
                clientBrand = "PvPLounge";
            } else if (clientBrand.contains("forge")) {
                clientBrand = "Forge";
            } else if (clientBrand.contains("salwyrr")) {
                clientBrand = "Salwyrr";
            } else if (clientBrand.contains("fabric")) {
                clientBrand = "Fabric";
            }

            profile.setClient(clientBrand);

        }, 20L);
    }

    @Override
    public void processSend(PacketSendEvent event) {

    }
}