package com.crystal.link2telegram.link2telegram;

public class Link2telegramAPI {
    Link2telegram Link2Telegram = new Link2telegram();
    public void sendMsg(String Msg){
        Link2Telegram.SendMessage(Msg,null,false);
    }
    public void sendFormatedMsg(String Msg, String MsgType) {
        Link2Telegram.SendMessage(Msg,MsgType,true);
    }
    public String getUpdatedText(){
        return Link2Telegram.GetUpdateText();
    }
}
