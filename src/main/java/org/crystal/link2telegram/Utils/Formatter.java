package org.crystal.link2telegram.Utils;

import org.crystal.link2telegram.plugin.Characters;

public class Formatter {
    // Format plugin variables like "&variable%"
    public static String PluginVariable(String configText, String VariableName, Object Variable){
        String[] GetText = configText.split("%");
        StringBuilder OutputString = new StringBuilder();
        for (String s : GetText) {
            if (!s.equals(VariableName)) { OutputString.append(s); }
            else { OutputString.append(Variable); }
        }
        return OutputString.toString();
    }
    // Format received messages whitch starts with "/" to command
    public static String[] BotCommand(int Length, String Text){
        StringBuilder Command = new StringBuilder();
        for (int i = 1; i < Length; i++) { Command.append(Text.charAt(i)); }
        return Command.toString().split(" ");
    }
    // Format a progress bar
    public static String ProgressBar(double val, double fullVal){
        double all = fullVal / 10;
        double fullCount = Math.floor(val / all);
        StringBuilder Blocks = new StringBuilder();
        for (int i = 0; i <= fullCount; i++){ Blocks.append(Characters.FULL_BLOCK); }
        double percentBlocks = all / 4;
        double Rem = val / all - Math.floor(val / all);
        if(Rem < (percentBlocks * 3)){ Blocks.append(Characters.LEFT_THREE_QUARTERS_BLOCK); }
        else if (Rem < (percentBlocks * 2)){ Blocks.append(Characters.LEFT_HALF_BLOCK); }
        else if (Rem < percentBlocks){ Blocks.append(Characters.LEFT_ONE_QUARTER_BLOCK); }
        double emptyBlocks = 10 - Blocks.length();
        for (int j = 0; j <= emptyBlocks; j++){ Blocks.append(Characters.LIGHT_SHADE); }
        return "[" + Blocks + "]";
    }
}
