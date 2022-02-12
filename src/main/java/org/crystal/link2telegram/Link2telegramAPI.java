package org.crystal.link2telegram;

public class Link2telegramAPI {
    private static Link2telegram L2t;
    public Link2telegramAPI(Link2telegram l2t){ L2t = l2t; }

    /**
     * Send message via TelegramBot
     * @param Msg Message to be sent
     */
    public void sendMsg(String Msg){ L2t.SendMessage(Msg,null,false); }
    /**
     * Send formatted message via TelegramBot
     * @param Msg Message to be sent
     * @param MsgType Message type to be sent(Status/Warn/Info)
     */
    public void sendFormatedMsg(String Msg, String MsgType) { L2t.SendMessage(Msg,MsgType,true); }
    /**
     * Get updated text
     * @return Updated text
     */
    public String getUpdatedText(){ return L2t.UpdateText; }
    /**
     * Get server TPS
     * @return Server TPS
     */
    public double[] getServerTPS() throws Throwable { return L2t.getRecentTpsReflector(); }
    /**
     * Get operating system CPU usage and Memory usage
     * @return Return an int[], build with {CPU usage, Memory usage}
     */
    public int[] getServerStatus(){ return (int[]) L2t.GetSystemStatus(false); }
}
