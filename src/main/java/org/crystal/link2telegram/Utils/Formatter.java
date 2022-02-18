package org.crystal.link2telegram.Utils;

public class Formatter {
    public static String PluginVariable(String configText, String VariableName, Object Variable){
        String[] GetText = configText.split("%");
        StringBuilder OutputString = new StringBuilder();
        for (String s : GetText) {
            if (!s.equals(VariableName)) { OutputString.append(s); }
            else { OutputString.append(Variable); }
        }
        return OutputString.toString();
    }
    public static String[] BotCommand(int Length, String Text){
        StringBuilder Command = new StringBuilder();
        for (int i = 1; i < Length; i++) { Command.append(Text.charAt(i)); }
        return Command.toString().split(" ");
    }
}
