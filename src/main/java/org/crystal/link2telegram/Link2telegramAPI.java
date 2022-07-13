package org.crystal.link2telegram;

import org.crystal.link2telegram.Utils.GetOnlinePlayers;
import org.crystal.link2telegram.Utils.GetSystemStatus;
import org.crystal.link2telegram.Utils.GetTPS;

public class Link2telegramAPI {
    private static Link2telegram L2t;
    public Link2telegramAPI(Link2telegram l2t){ L2t = l2t; }

    /**
     * Send message via TelegramBot
     * @param SendTo Chat ID to be send
     * @param Msg Message to be sent
     */
    public void sendMsg(String SendTo, String Msg){ L2t.SendMessage(SendTo, Msg, null,false,false); }
    /**
     * Send message via TelegramBot
     * @param SendTo Chat ID to be send
     * @param Msg Message to be sent
     * @param SyncMsg Sync message to other chats
     */
    public void sendMsg(String SendTo, String Msg, boolean SyncMsg) { L2t.SendMessage(SendTo, Msg, null, true, SyncMsg); }
    /**
     * Send message via TelegramBot
     * @param SendTo Chat ID to be send
     * @param Msg Message to be sent
     * @param MsgType Message type to be format
     */
    public void sendMsg(String SendTo, String Msg, String MsgType) { L2t.SendMessage(SendTo, Msg, MsgType, true, false); }
    /**
     * Send message via TelegramBot
     * @param SendTo Chat ID to be send
     * @param Msg Message to be sent
     * @param MsgType Message type to be format
     * @param SyncMsg Sync message to other chats
     */
    public void sendMsg(String SendTo, String Msg, String MsgType, boolean SyncMsg) { L2t.SendMessage(SendTo, Msg, MsgType,true, SyncMsg); }
    /**
     * Get server TPS
     * @return Server TPS
     */
    public double[] getServerTPS() throws Throwable { return GetTPS.Get(); }
    /**
     * Get operating system CPU usage and Memory usage
     * @return Return an int[], build with {CPU usage, Memory usage}
     */
    public int[] getServerStatus(){ return (int[]) GetSystemStatus.Get(false); }
    /**
     * Get a list of online players
     * @return Return a String[], build with {Player list, Player count}
     */
    public String[] getOnlinePlayers(){ return GetOnlinePlayers.GetList(); }
}
