package org.crystal.link2telegram.Utils;

import org.bukkit.entity.Player;

public class GetOnlinePlayers {
    public static String Get(){
        StringBuilder playerString = new StringBuilder();
        playerString.append("Online players:\n");
        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            playerString.append(player.getName()).append("\n");
        }
        return playerString.toString();
    }
}
