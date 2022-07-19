package org.crystal.link2telegram.Utils;

import org.crystal.link2telegram.Link2telegram;

public class SendCommand {
    private static Link2telegram L2t;
    public SendCommand(Link2telegram l2t){ L2t = l2t; }

    public static void Send(String Command){
        StringBuilder command = new StringBuilder();
        for (int i = 1; i < Command.length(); i++) { command.append(Command.charAt(i)); }
        String[] CommandArray = command.toString().split(" "); // Split String with space
        StringBuilder OriginalCommand = new StringBuilder();
        for (int j =1; j < CommandArray.length; j++){ OriginalCommand.append(CommandArray[j]).append(" "); }
        L2t.SendConsoleCommand(OriginalCommand.toString());
    }
}
