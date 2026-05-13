package me.arrow.checks.impl.misc.timer;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import me.arrow.checks.annotations.Experimental;
import me.arrow.checks.enums.CheckType;
import me.arrow.checks.types.Check;
import me.arrow.enums.MsgType;
import me.arrow.managers.profile.Profile;

import static com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client.*;
import static com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server.PLAYER_ROTATION;

// idk man basic slowed down timer check, falses alot on modern so i ignore standing still

@Experimental
public class TimerB extends Check {

    public TimerB(Profile profile) {
        super(profile, CheckType.TIMER, "B", "Checks for slowed down game time");
    }

    private long lastPacket = -1337L;

    private double threshold;


    @Override
    public void handle(PacketSendEvent event) {

    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType().equals(PLAYER_FLYING)
                || event.getPacketType().equals(PLAYER_POSITION)
                || event.getPacketType().equals(PLAYER_ROTATION)
                || event.getPacketType().equals(PLAYER_POSITION_AND_ROTATION)) {

            if (profile.getMovementData().getMovingTicks() < 10 || profile.shouldCancel()) return;

            long now = System.currentTimeMillis();
            long delta = now - this.lastPacket;

            if (profile.getTick() > 20 && this.lastPacket > -1337L) {
                if (delta > 100L) {
                    if (++threshold > 8) {
                        fail("Slowed Down Time", "delta " + MsgType.MAIN_THEME_COLOR.getMessage() + delta
                                + "\nlastPacket " + MsgType.MAIN_THEME_COLOR.getMessage() + lastPacket );
                        threshold = 0;
                    }
                } else {
                    threshold -= Math.min(threshold, 0.7f);
                }
            }

            this.lastPacket = now;
        }
    }
}