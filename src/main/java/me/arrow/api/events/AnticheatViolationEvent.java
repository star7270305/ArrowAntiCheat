package me.arrow.api.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AnticheatViolationEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    /**
     * -- GETTER --
     *
     * @return The player involved in this event
     */
    @Getter
    private final Player player;
    private final String checkName;
    /**
     * -- GETTER --
     *
     * @return The check's description
     */
    @Getter
    private final String description;
    /**
     * -- GETTER --
     *
     * @return The type of the check included in this event
     */
    @Getter
    private final String type;
    /**
     * -- GETTER --
     *
     * @return The information of why the player failed this check
     */
    @Getter
    private final String information;
    @Getter
    private final String informationTitle;
    /**
     * -- GETTER --
     *
     * @return The total violation amount
     */
    @Getter
    private final int vl;
    /**
     * -- GETTER --
     *
     * @return The maximum violation amount
     */
    @Getter
    private final int maxVl;
    /**
     * -- GETTER --
     *
     * @return Whether the check is in an experimental state
     */
    @Getter
    private final boolean experimental;
    private boolean cancel = false;

    /**
     * This event will always be called async, Beware.
     */
    public AnticheatViolationEvent(Player player, String checkName, String description, String type, String informationTitle, String information,
                                   int vl, int maxVl, boolean experimental) {
        super(true);
        this.player = player;
        this.checkName = checkName;
        this.description = description;
        this.type = type;
        this.informationTitle = informationTitle;
        this.information = information;
        this.vl = vl;
        this.maxVl = maxVl;
        this.experimental = experimental;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * @return The check included in this event
     */
    public String getCheck() {
        return checkName;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}