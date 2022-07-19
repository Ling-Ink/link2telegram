package org.crystal.link2telegram.Utils;

import org.crystal.link2telegram.Link2telegram;

public class SendCommand {
    private static Link2telegram L2t;
    public SendCommand(Link2telegram l2t){ L2t = l2t; }

    public static void Send(String[] CommandArray){
        StringBuilder OriginalCommand = new StringBuilder();
        for (int j =1; j < CommandArray.length; j++){ OriginalCommand.append(CommandArray[j]).append(" "); }
        L2t.SendConsoleCommand(OriginalCommand.toString());
    }
}
