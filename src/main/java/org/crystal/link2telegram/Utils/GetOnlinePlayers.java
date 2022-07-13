package org.crystal.link2telegram.Utils;

import org.bukkit.entity.Player;

public class GetOnlinePlayers {
    public static String Get(){
        StringBuilder playerString = new StringBuilder();
        StringBuilder returnString = new StringBuilder();
        int playerCount = 0;
        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            playerString.append(player.getName()).append("\n");
            playerCount += 1;
        }
        returnString.append("Online players:[").append(playerCount).append("]\n").append(playerString);
        return returnString.toString();
    }
    public static String[] GetList(){
        StringBuilder playerString = new StringBuilder();
        int playerCount = 0;
        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            playerString.append(player.getName()).append("\n");
            playerCount += 1;
        }
        return new String[]{ playerString.toString(), Integer.toString(playerCount) };
    }
}
