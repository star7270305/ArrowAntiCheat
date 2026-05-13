package me.arrow.utils.customutils;


import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import me.arrow.Arrow;
import me.arrow.utils.customutils.raytrace.RayCollision;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collection;

public class MiscUtils {

    //private static WrappedClass materialClass = new WrappedClass(Material.class);
    public static Material match(String material) {
        return Material.matchMaterial(material);
    }


    public static void drawRay(RayCollision collision, Particle particle, Collection<? extends Player> players) {
        for (double i = 0; i < 8; i += 0.2) {
            float fx = (float) (collision.originX + (collision.directionX * i));
            float fy = (float) (collision.originY + (collision.directionY * i));
            float fz = (float) (collision.originZ + (collision.directionZ * i));
            Vector3d vec = new Vector3d(fx, fy, fz);
            Vector3f vecOffset = new Vector3f(0, 0, 0);
            WrapperPlayServerParticle packet = new WrapperPlayServerParticle(particle, true,
                    vec, vecOffset, 0, 0);
            players.forEach(p -> Arrow.getInstance().getProfileManager().getProfile(p).sendPacket(packet));
        }
    }

//    public static void drawCuboid(SimpleCollisionBox box, Particle particle, Collection<? extends Player> players) {
//        Step.GenericStepper<Float> x = Step.step((float) box.xMin, 0.241f, (float) box.xMax);
//        Step.GenericStepper<Float> y = Step.step((float) box.yMin, 0.241f, (float) box.yMax);
//        Step.GenericStepper<Float> z = Step.step((float) box.zMin, 0.241f, (float) box.zMax);
//        for (float fx : x) {
//            for (float fy : y) {
//                for (float fz : z) {
//                    int check = 0;
//                    if (x.first() || x.last()) check++;
//                    if (y.first() || y.last()) check++;
//                    if (z.first() || z.last()) check++;
//                    if (check >= 2) {
//                        Vector3d vec = new Vector3d(fx, fy, fz);
//                        Vector3f vecOffset = new Vector3f(0, 0, 0);
//                        WrapperPlayServerParticle packet = new WrapperPlayServerParticle(particle, true,
//                                vec, vecOffset, 0, 0);
//                        for (Player p : players) {
//                            Arrow.getInstance().getProfileManager().getProfile(p).sendPacket(packet);
//                        }
//                    }
//                }
//            }
//        }
//    }
}
