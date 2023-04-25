package com.fendyk.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ProxyPlayerAuthenticatedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    public ProxyPlayerAuthenticatedEvent() {

    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
