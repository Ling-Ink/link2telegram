package com.crystal.link2telegram.link2telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import okhttp3.OkHttpClient;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Calendar;

public final class Link2telegram extends JavaPlugin {
    TelegramBot bot;

    private void InitializeBot(){
        String ProxyHostname = this.getConfig().getString("Proxy.Hostname");
        int ProxyPort = this.getConfig().getInt("Proxy.Port");
        if(ProxyHostname != null){
            OkHttpClient client = new OkHttpClient.Builder()
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ProxyHostname, ProxyPort)))
                    .build();
            bot = new TelegramBot.Builder(this.getConfig().getString("BotToken")).okHttpClient(client).build();
        } else {
            bot = new TelegramBot(this.getConfig().getString("BotToken"));
        }
    }

    private String FormatMsg(String UnformattedMsg,String Type){
        Calendar cal=Calendar.getInstance();
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int m = cal.get(Calendar.MINUTE);
        int s = cal.get(Calendar.SECOND);
        String time = h + ":" + m + ":" + s + "\n";
        return switch (Type) {
            case "Status" -> "[状态]" + time + UnformattedMsg;
            case "Warn" -> "[警告]" + time + UnformattedMsg;
            default -> "[信息]" + time + UnformattedMsg;
        };
    }

    private void SendMessage(String Msg, String MsgType){
        bot.execute(new SendMessage(this.getConfig().getString("SendMsgToChatID"), FormatMsg(Msg,MsgType))).toString();
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        InitializeBot();
        this.getLogger().info("Plugin Enabled!");
        SendMessage(this.getConfig().getString("DefaultMsg.PluginOnEnableMsg"),"Status");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Plugin Disabled!");
        SendMessage(this.getConfig().getString("DefaultMsg.PluginOnDisableMsg"),"Status");
    }
}
