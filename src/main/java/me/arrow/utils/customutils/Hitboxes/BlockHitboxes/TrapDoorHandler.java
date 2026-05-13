package me.arrow.utils.customutils.Hitboxes.BlockHitboxes;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import me.arrow.utils.customutils.CollisionFactory;
import me.arrow.utils.customutils.Hitboxes.CollisionBox;
import me.arrow.utils.customutils.Hitboxes.GeneralHitboxes.SimpleCollisionBox;
import org.bukkit.block.Block;

public class TrapDoorHandler implements CollisionFactory {
    @Override
    public CollisionBox fetch(ClientVersion version, Block block) {
        byte data = block.getState().getData().getData();
        double var2 = 0.1875;

        if ((data & 4) != 0) {
            if ((data & 3) == 0) {
                return new SimpleCollisionBox(0.0, 0.0, 1.0 - var2, 1.0, 1.0, 1.0);
            }

            if ((data & 3) == 1) {
                return new SimpleCollisionBox(0.0, 0.0, 0.0, 1.0, 1.0, var2);
            }

            if ((data & 3) == 2) {
                return new SimpleCollisionBox(1.0 - var2, 0.0, 0.0, 1.0, 1.0, 1.0);
            }

            if ((data & 3) == 3) {
                return new SimpleCollisionBox(0.0, 0.0, 0.0, var2, 1.0, 1.0);
            }
        } else {
            if ((data & 8) != 0) {
                return new SimpleCollisionBox(0.0, 1.0 - var2, 0.0, 1.0, 1.0, 1.0);
            } else {
                return new SimpleCollisionBox(0.0, 0.0, 0.0, 1.0, var2, 1.0);
            }
        }
        return null;
    }
}
