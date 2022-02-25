package org.crystal.link2telegram;

import org.crystal.link2telegram.Utils.GetSystemStatus;
import org.crystal.link2telegram.Utils.GetTPS;

public class Link2telegramAPI {
    private static Link2telegram L2t;
    public Link2telegramAPI(Link2telegram l2t){ L2t = l2t; }

    /**
     * Send message via TelegramBot
     * @param Msg Message to be sent
     */
    public void sendMsg(String Msg){ L2t.SendMessage(Msg,null,false); }
    public void sendMsg(String Msg, String MsgType) { L2t.SendMessage(Msg,MsgType,true); }
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
}
