package me.arrow.playerdata.data.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.arrow.managers.profile.Profile;

@Getter
@AllArgsConstructor
public class IReachData {
    private final double distance;
    private final double distanceNo003;
    private final boolean validHitbox;
    private final boolean attack;
    private final boolean interact;
    private final Profile profile;
}