package org.crystal.link2telegram.Utils;

import org.bukkit.Bukkit;
import org.crystal.link2telegram.Link2telegram;

public class SendBukkitCommand {
    // Used to send commands to minecraft server
    public static void Send(String Command){
        StringBuilder SBCommand = new StringBuilder();
        for (int i = 1; i < Command.length(); i++) { SBCommand.append(Command.charAt(i)); }
        String[] CommandArray = SBCommand.toString().split(" "); // Split String with space
        StringBuilder OriginalCommand = new StringBuilder();
        for (int j =1; j < CommandArray.length; j++){ OriginalCommand.append(CommandArray[j]).append(" "); }
        Bukkit.getScheduler().runTask(new Link2telegram(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),OriginalCommand.toString()));
    }
}
