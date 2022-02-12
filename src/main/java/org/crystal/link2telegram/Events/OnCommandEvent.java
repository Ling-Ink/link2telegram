package org.crystal.link2telegram.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class OnCommandEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    String Command;
    public OnCommandEvent(String Command){ this.Command = Command; }
    public String[] GetCommand(){ return Command.split(" "); }
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
