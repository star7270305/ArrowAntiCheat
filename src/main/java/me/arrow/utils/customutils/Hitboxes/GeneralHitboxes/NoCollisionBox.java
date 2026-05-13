package me.arrow.utils.customutils.Hitboxes.GeneralHitboxes;

import com.github.retrooper.packetevents.protocol.particle.Particle;

import me.arrow.utils.customutils.Hitboxes.CollisionBox;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class NoCollisionBox implements CollisionBox {

    public static final NoCollisionBox INSTANCE = new NoCollisionBox();

    private NoCollisionBox() { }

    @Override
    public boolean isCollided(CollisionBox other) {
        return false;
    }

    @Override
    public CollisionBox offset(double x, double y, double z) {
        return this;
    }

    @Override
    public void downCast(List<SimpleCollisionBox> list) { /**/ }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public CollisionBox copy() {
        return this;
    }

    @Override
    public void draw(Particle particle, Collection<? extends Player> players) {
        // ...?
    }
}
