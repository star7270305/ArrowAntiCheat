package me.arrow.utils.customutils;


import lombok.Getter;
import me.arrow.managers.profile.Profile;

public class EventTimer {
    @Getter
    private int tick;
    private final int max;
    private final Profile user;

    public EventTimer(int max, Profile user) {
        this.tick = 0;
        this.max = max;
        this.user = user;
        reset();
    }

    public boolean hasNotPassed() {
        int maxTick = this.max;
        return (user.getTick() > maxTick && (user.getTick() - tick) < maxTick);
    }

    public boolean passed() {
        int maxTick = this.max;
        return (user.getTick() > maxTick && (user.getTick() - tick) > maxTick);
    }

    public boolean hasNotPassed(int ctick) {
        return (user.getTick() > ctick && (user.getTick() - tick) < ctick);
    }

    public boolean passed(int ctick) {
        return (user.getTick() > ctick && (user.getTick() - tick) > ctick);
    }

    public void reset() {
        this.tick = user.getTick();
    }

}

