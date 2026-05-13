package me.arrow.api.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class VerboseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final String checkName;
    private final String type;
    private final String information;
    private final double vl;
    private final double maxVl;

    public VerboseEvent(Player player, String checkName, String type, String information, double vl, double maxVl) {
        super(true);
        this.player = player;
        this.checkName = checkName;
        this.type = type;
        this.information = information;
        this.vl = vl;
        this.maxVl = maxVl;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
