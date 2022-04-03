package org.crystal.link2telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import okhttp3.OkHttpClient;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.crystal.link2telegram.Events.GetUpdateEvent;
import org.crystal.link2telegram.Events.OnCommandEvent;
import org.crystal.link2telegram.Utils.*;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Calendar;
import java.util.Objects;

public class Link2telegram extends JavaPlugin implements Listener {
    private static Link2telegramAPI L2tAPI;
    public static Link2telegramAPI L2tAPI(){ return L2tAPI; }
    private String GetStringConfig(String path){ return this.getConfig().getString(path); }
    private int GetIntConfig(String path){ return this.getConfig().getInt(path); }
    private void onEnableMsg(){
        this.getLogger().info("#     Link2telegram     #");
        this.getLogger().info("#   Version 1.1.3-pre   #");
    }

    private TelegramBot bot;

    @Override public void onEnable() {
        Metrics metrics = new Metrics(this, 14304);
        L2tAPI = new Link2telegramAPI(this);
        this.saveDefaultConfig();
        InitializeBotAbout();
        if(this.getConfig().getBoolean("ServerStart/StopMessage.Enabled")){
            SendMessage(GetStringConfig("ServerStart/StopMessage.PluginOnEnableMsg"),"Status",true);
        }
        getServer().getPluginManager().registerEvents(this, this);
        onEnableMsg();
    }
    @Override public void onDisable() {
        this.getLogger().info("Plugin Disabled!");
        if(this.getConfig().getBoolean("ServerStart/StopMessage.Enabled")){
            SendMessage(GetStringConfig("ServerStart/StopMessage.PluginOnDisableMsg"),"Status",true);
        }
    }
    @Override public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("l2trestart")) {
            if (!(sender instanceof Player)) { InitializeBotAbout(); }
            else { sender.sendMessage("This command can only be used in console."); }
            return true;
        }
        return false;
    }
    private void InitializeBotAbout(){
        InitializeBot();
        ListenUpdateText();
        if(this.getConfig().getBoolean("TPSMonitor.Enabled")){ TPSListener(); }
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

    protected void SendMessage(String Msg, String MsgType, boolean FormatMsg){
        if(FormatMsg){
            Calendar cal=Calendar.getInstance();
            String Message;
            String time = cal.get(Calendar.HOUR_OF_DAY) + " : " + cal.get(Calendar.MINUTE) + " : " + cal.get(Calendar.SECOND) + "\n";
            switch (MsgType){
                case "Status" -> Message = Characters.STATUS_ICON + " [Status] " + time + Msg;
                case "Warn" -> Message = Characters.WARING_ICON + " [Warn] " + time + Msg;
                case "Info" -> Message = Characters.INFO_ICON + " [Info] " + time + Msg;
                default -> Message = time + Msg;
            }
            bot.execute(new SendMessage(this.getConfig().getString("SendMsgToChatID"), Message));
        } else {
            bot.execute(new SendMessage(this.getConfig().getString("SendMsgToChatID"), Msg));
        }
    }
    private void ListenUpdateText(){
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                if (update.message() != null && update.message().chat() != null) {
                    String[] GetUpdatedTextArray = update.message().text().split(" ");
                    if(Objects.equals(GetUpdatedTextArray[0], "/status")){
                        SendMessage((String) GetSystemStatus.Get(true),"Info",true);
                    } else if(Objects.equals(GetUpdatedTextArray[0], "/sudo")){
                        SendBukkitCommand.Send(update.message().text());
                    } else if (GetUpdatedTextArray[0].startsWith("/")){
                        OnCommandEvent OnCommandEvent = new OnCommandEvent(Formatter.BotCommand(update.message().text().length(), update.message().text()));
                        Bukkit.getScheduler().runTask(this, () -> Bukkit.getServer().getPluginManager().callEvent(OnCommandEvent));
                    } else {
                        GetUpdateEvent GetUpdateEvent = new GetUpdateEvent(update.message().text());
                        Bukkit.getScheduler().runTask(this, () -> Bukkit.getServer().getPluginManager().callEvent(GetUpdateEvent));
                    }
                    this.getLogger().info(update.message().text());
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    @EventHandler private void playerLogin(PlayerLoginEvent event){
        SendMessage(
                Formatter.PluginVariable(
                        GetStringConfig("PlayerLogin.PlayerLoginMessage"),
                        "player",
                        event.getPlayer().getName()
                ),
                "Info",
                true
        );
    }

    private void TPSListener(){
        new BukkitRunnable(){
            double[] TPS;
            @Override public void run(){
                try { TPS = GetTPS.Get(); }
                catch (Throwable ignored) { }
                if(TPS[0] > GetIntConfig("TPSMonitor.MaxTPSThreshold")){
                    SendMessage(
                            Formatter.PluginVariable(
                                    GetStringConfig("TPSMonitor.TPSTooHighInformation"),
                                    "TPS",
                                    TPS[0]
                            ),
                            "Warn",
                            true
                    );
                } else if (TPS[0] < GetIntConfig("TPSMonitor.MinTPSThreshold")){
                    SendMessage(
                            Formatter.PluginVariable(
                                    GetStringConfig("TPSMonitor.TPSTooLowInformation"),
                                    "TPS",
                                    TPS[0]
                            ),
                            "Warn",
                            true
                    );
                }
            }
        }.runTaskTimer(this,
                0,
                20L * this.getConfig().getInt("TPSMonitor.TPSCheckTimeout")
        );
    }
}
