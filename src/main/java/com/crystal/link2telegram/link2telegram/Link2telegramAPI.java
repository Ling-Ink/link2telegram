package com.crystal.link2telegram.link2telegram;

public class Link2telegramAPI {
    public void sendMsg(String Msg){
        Link2telegram Link2Telegram = new Link2telegram();
        Link2Telegram.APISendMessage("Msg");
    }
}
