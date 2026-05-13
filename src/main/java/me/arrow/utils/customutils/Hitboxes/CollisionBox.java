package me.arrow.utils.customutils.Hitboxes;

import com.github.retrooper.packetevents.protocol.particle.Particle;
import me.arrow.utils.customutils.Hitboxes.GeneralHitboxes.SimpleCollisionBox;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public interface CollisionBox {
    boolean isCollided(CollisionBox other);
    void draw(Particle particle, Collection<? extends Player> players);
    CollisionBox copy();
    CollisionBox offset(double x, double y, double z);
    void downCast(List<SimpleCollisionBox> list);
    boolean isNull();
}