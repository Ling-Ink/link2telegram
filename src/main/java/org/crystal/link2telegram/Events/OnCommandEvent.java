package org.crystal.link2telegram.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

// Get command event, used to get received telegram bot commands
// Cerate a event listener to listen commands except build-in commands
public class OnCommandEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    String[] Command;
    String ChatId;
    public OnCommandEvent(String[] Command, String ChatId){ this.Command = Command; this.ChatId = ChatId; }
    public String[] GetCommand(){ return Command; }
    public String GetChatId(){ return ChatId; }
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
