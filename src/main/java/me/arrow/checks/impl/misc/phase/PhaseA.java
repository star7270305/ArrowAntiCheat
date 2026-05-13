package me.arrow.checks.impl.misc.phase;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import me.arrow.checks.annotations.Experimental;
import me.arrow.checks.enums.CheckType;
import me.arrow.checks.types.Check;
import me.arrow.managers.profile.Profile;

// idk, if you manage to make a perfect world compensasion, which block processor is almost there
// then feel free to make a raytraced check for deltaXZ under 0.28 or whatever the ground speed is

@Experimental
public class PhaseA extends Check {

    public PhaseA(Profile profile) {
        super(profile, CheckType.PHASE, "A", "Checks if the player is clipping through a block");
    }

    @Override
    public void handle(PacketSendEvent event) {

    }

    @Override
    public void handle(PacketReceiveEvent event) {

    }
}
