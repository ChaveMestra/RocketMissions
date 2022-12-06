package com.rocketmc.quests.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class PlayerCrimeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private Player player;


    public PlayerCrimeEvent(Player player) {
        this.player = player;


    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
