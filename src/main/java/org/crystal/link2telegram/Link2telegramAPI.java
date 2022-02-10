package org.crystal.link2telegram;

public class Link2telegramAPI {
    Link2telegram Link2Telegram = new Link2telegram();
    public void sendMsg(String Msg){ Link2Telegram.SendMessage(Msg,null,false); }
    public void sendFormatedMsg(String Msg, String MsgType) { Link2Telegram.SendMessage(Msg,MsgType,true); }
    public String getUpdatedText(){ return Link2Telegram.GetUpdateText(); }
    public double[] getServerTPS() throws Throwable { return Link2Telegram.getRecentTpsRefl(); }
    public int[] getServerStatus(){ return Link2Telegram.FormatSystemStatus(); }
}
