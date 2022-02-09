package com.crystal.link2telegram.link2telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import okhttp3.OkHttpClient;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Calendar;

public class Link2telegram extends JavaPlugin {
    private final String STATUS_ICON = "\uD83D\uDCCA";
    private final String WARING_ICON = "⚠️";
    private final String INFO_ICON = "ℹ️";
    private static Field recentTps;
    static Object minecraftServer;
    String UpdateText;
    TelegramBot bot;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        InitializeBot();
        ListenUpdateText();
        if(this.getConfig().getBoolean("TPSMonitor.Enabled")){ TPSListener(); }
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

    protected double[] getRecentTpsRefl() throws Throwable {
        if (minecraftServer == null) {
            Server server = Bukkit.getServer();
            Field consoleField = server.getClass().getDeclaredField("console");
            consoleField.setAccessible(true);
            minecraftServer = consoleField.get(server);
        }
        if (recentTps == null) {
            recentTps = minecraftServer.getClass().getSuperclass().getDeclaredField("recentTps");
            recentTps.setAccessible(true);
        }
        return (double[]) recentTps.get(minecraftServer);
    }
    private void TPSListener(){
        new BukkitRunnable(){
            @Override
            public void run(){
                try { JudgeTPS(getRecentTpsRefl()); }
                catch (Throwable e) { e.printStackTrace(); }
            }
        }.runTaskTimer(this,0,20L * this.getConfig().getInt("TPSMonitor.TPSCheckTimeout"));
    }
    private void JudgeTPS(double[] TPS){
        if(TPS[0] > this.getConfig().getInt("TPSMonitor.MaxTPSThreshold")){
            if(this.getConfig().getBoolean("TPSMonitor.THIEndedWithTPS"))
                { SendMessage(this.getConfig().getString("TPSMonitor.TPSTooHighInformation") + TPS[0],"Warn",true); }
            else{ SendMessage(this.getConfig().getString("TPSMonitor.TPSTooHighInformation"),"Warn",true); }
        } else if (TPS[0] < this.getConfig().getInt("TPSMonitor.MinTPSThreshold")){
            if(this.getConfig().getBoolean("TPSMonitor.TLIEndedWithTPS"))
                { SendMessage(this.getConfig().getString("TPSMonitor.TPSTooLowInformation") + TPS[0],"Warn",true); }
            else{ SendMessage(this.getConfig().getString("TPSMonitor.TPSTooLowInformation"),"Warn",true); }
        }
    }
}
