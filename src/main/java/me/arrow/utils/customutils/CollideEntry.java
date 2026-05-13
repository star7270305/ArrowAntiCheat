package me.arrow.utils.customutils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.arrow.utils.customutils.BlockUtils.BlockUtil;
import me.arrow.utils.customutils.Hitboxes.GeneralHitboxes.BoundingBox;
import org.bukkit.block.Block;

@AllArgsConstructor
@Getter
public class CollideEntry {
    private final Block block;
    private final BoundingBox boundingBox;

    public boolean isChunkLoaded() {
        try {
            return BlockUtil.isChunkLoaded(block.getLocation());
        }
        catch (Exception ignored){
            return false;
        }
    }
}

