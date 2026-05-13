package me.arrow.utils.customutils.Hitboxes.BlockHitboxes;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import me.arrow.utils.customutils.CollisionFactory;
import me.arrow.utils.customutils.Hitboxes.CollisionBox;
import me.arrow.utils.customutils.Hitboxes.GeneralHitboxes.SimpleCollisionBox;
import org.bukkit.block.Block;

@SuppressWarnings("Duplicates")
public class DynamicRod implements CollisionFactory {

    public static final CollisionBox UD = new SimpleCollisionBox(0.4375,0, 0.4375, 0.5625, 1, 0.625);
    public static final CollisionBox EW = new SimpleCollisionBox(0,0.4375, 0.4375, 1, 0.5625, 0.625);
    public static final CollisionBox NS = new SimpleCollisionBox(0.4375, 0.4375, 0, 0.5625, 0.625, 1);

    @Override
    public CollisionBox fetch(ClientVersion version, Block b) {
        return switch (b.getData()) {
            case 2, 3 -> NS.copy();
            case 4, 5 -> EW.copy();
            default -> UD.copy();
        };
    }

}
