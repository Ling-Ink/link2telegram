package org.crystal.link2telegram;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GetUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    String Message;
    public GetUpdateEvent(String Message){
        this.Message = Message;
    }
    public String GetMessage(){
        return Message;
    }
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }
}
