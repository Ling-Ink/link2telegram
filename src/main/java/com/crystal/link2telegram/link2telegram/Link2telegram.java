package com.crystal.link2telegram.link2telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import okhttp3.OkHttpClient;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Calendar;

public class Link2telegram extends JavaPlugin {
    private final String STATUS_ICON = "\uD83D\uDCCA";
    private final String WARING_ICON = "⚠️";
    private final String INFO_ICON = "ℹ️";
    String UpdateText;
    TelegramBot bot;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        InitializeBot();
        ListenUpdateText();
        this.getLogger().info("Plugin Enabled!");
        SendMessage(this.getConfig().getString("DefaultMsg.PluginOnEnableMsg"),"Status",true);
    }
    @Override
    public void onDisable() {
        this.getLogger().info("Plugin Disabled!");
        SendMessage(this.getConfig().getString("DefaultMsg.PluginOnDisableMsg"),"Status",true);
    }

    private void InitializeBot(){
        String ProxyHostname = this.getConfig().getString("Proxy.Hostname");
        int ProxyPort = this.getConfig().getInt("Proxy.Port");
        if(ProxyHostname != null){
            OkHttpClient client = new OkHttpClient.Builder()
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ProxyHostname, ProxyPort)))
                    .build();
            bot = new TelegramBot.Builder(this.getConfig().getString("BotToken")).okHttpClient(client).build();
        } else { bot = new TelegramBot(this.getConfig().getString("BotToken")); }
    }

    private void ListenUpdateText(){
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                if (update.message() != null && update.message().chat() != null) {
                    Message message = update.message();
                    SetUpdateText(message.text());
                    this.getLogger().info(message.text());
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
    private void SetUpdateText(String Text){ UpdateText = Text; }
    protected String GetUpdateText(){ return UpdateText; }

    protected void SendMessage(String Msg, String MsgType, boolean FormatMsg){
        if(FormatMsg){ bot.execute(new SendMessage(this.getConfig().getString("SendMsgToChatID"), FormatMsg(Msg,MsgType))); }
        else { bot.execute(new SendMessage(this.getConfig().getString("SendMsgToChatID"), Msg)); }
    }
    private String FormatMsg(String UnformattedMsg,String Type){
        Calendar cal=Calendar.getInstance();
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int m = cal.get(Calendar.MINUTE);
        int s = cal.get(Calendar.SECOND);
        String time = h + " : " + m + " : " + s + "\n";
        return switch (Type) {
            case "Status" -> STATUS_ICON + " [状态] " + time + UnformattedMsg;
            case "Warn" -> WARING_ICON + " [警告] " + time + UnformattedMsg;
            case "Info" -> INFO_ICON + " [信息] " + time + UnformattedMsg;
            default -> time + UnformattedMsg;
        };
    }

}
