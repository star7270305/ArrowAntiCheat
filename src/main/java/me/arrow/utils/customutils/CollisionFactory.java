package me.arrow.utils.customutils;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import me.arrow.utils.customutils.Hitboxes.CollisionBox;
import org.bukkit.block.Block;

public interface CollisionFactory {
    CollisionBox fetch(ClientVersion version, Block block);
}
