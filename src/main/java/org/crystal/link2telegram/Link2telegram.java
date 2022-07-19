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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.crystal.link2telegram.Events.GetUpdateEvent;
import org.crystal.link2telegram.Events.OnCommandEvent;
import org.crystal.link2telegram.Utils.*;
import org.crystal.link2telegram.Utils.Formatter;
import org.crystal.link2telegram.plugin.Characters;
import org.crystal.link2telegram.plugin.Configuration;
import org.crystal.link2telegram.plugin.Messages;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

public class Link2telegram extends JavaPlugin implements Listener {
    private static Link2telegramAPI L2tAPI;
    public static Link2telegramAPI L2tAPI(){ return L2tAPI; }
    private static ReadConfig ReadConf;
    public static ReadConfig ReadConf(){ return ReadConf; }
    private static SendCommand SendCmd;
    public static SendCommand SendCmd(){ return SendCmd; }
    private TelegramBot telegramBot;

    public String getStringConfig(String path){ return this.getConfig().getString(path); }
    public Boolean getBooleanConfig(String path){ return this.getConfig().getBoolean(path); }
    public Integer getIntegerConfig(String path){ return this.getConfig().getInt(path); }
    public List<String> getStringListConfig(String path){ return this.getConfig().getStringList(path); }

    public void SendConsoleCommand(String command){
        Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command));
    }

    private void onEnableMsg(){ // Startup message
        StringBuilder SyncMsgToString = new StringBuilder();
        for (String s : Configuration.SYNC_MSG_TO()) {
            try {
                SyncMsgToString.append(s).append(" ");
            } catch (Exception ignored) { }
        }
        this.getLogger().info("#     Link2telegram     #");
        this.getLogger().info("#     Version 1.3.2     #");
        this.getLogger().info("# Plugin owner ID: " + Configuration.OWNER_CHAT_ID());
        this.getLogger().info("# Message send to: " + SyncMsgToString);
        this.getLogger().info("===== Configurations =====");
        this.getLogger().info("# Proxy_Hostname: " + Configuration.PROXY_HOSTNAME());
        this.getLogger().info("# Proxy_Port: " + Configuration.PROXY_PORT());
        this.getLogger().info("# TPS_Check_Timeout: " + Configuration.TPS_CHECK_TIMEOUT());
        this.getLogger().info("# TPS_Max_Threshold: " + Configuration.TPS_MAX_THRESHOLD());
        this.getLogger().info("# TPS_Min_Threshold: " + Configuration.TPS_MIN_THRESHOLD());
    }

    @Override public void onEnable() { // When plugin enabled, get and send enabled massage from config.yml
        Metrics metrics = new Metrics(this, 14304);
        SendCmd = new SendCommand(this);
        L2tAPI = new Link2telegramAPI(this);
        ReadConf = new ReadConfig(this);
        this.saveDefaultConfig();
        ReadConfig.Read();
        InitializeBot();
        if(Configuration.ENABLE_START_STOP_MSG()){
            SendMessage(Configuration.OWNER_CHAT_ID(), Messages.PLUGIN_ON_ENABLE(),"Status",true, true);
        }
        getServer().getPluginManager().registerEvents(this, this);
        onEnableMsg();
    }
    @Override public void onDisable() {
        this.getLogger().info("Plugin Disabled!");
        if(Configuration.ENABLE_START_STOP_MSG()){
            SendMessage(Configuration.OWNER_CHAT_ID(), Messages.PLUGIN_ON_DISABLE(),"Status",true, true);
        }
    }
    // Listen restart command and restart plugin
    @Override public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            if (cmd.getName().equalsIgnoreCase("l2treload")) {
                this.getLogger().info("Reload Configurations");
                ReadConfig.Read();
                onEnableMsg();
                return true;
            } else if (cmd.getName().equalsIgnoreCase("l2treinit")) {
                this.getLogger().info("Reinitialize Bot");
                InitializeBot();
                return true;
            }
        } else { sender.sendMessage("This command can only be used in console."); }
        return false;
    }

    private void InitializeBot(){ // Set proxy and bot
        if(Configuration.PROXY_HOSTNAME() != null){
            OkHttpClient client = new OkHttpClient.Builder()
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                            Configuration.PROXY_HOSTNAME(),
                            Configuration.PROXY_PORT())))
                    .build();
            telegramBot = new TelegramBot.Builder(Configuration.BOT_TOKEN()).okHttpClient(client).build();
        } else { telegramBot = new TelegramBot(Configuration.BOT_TOKEN()); }
        ListenUpdateText();
        if(Configuration.ENABLE_TPS_MONITOR())
        { TPSListener(); }
    }

    private void ListenUpdateText(){
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                if (update.message() != null && update.message().chat() != null) {
                    List<String> GetUpdatedTextArray;
                    String ChatId = update.message().chat().id().toString();
                    String ChatName = update.message().chat().username();
                    try {
                        GetUpdatedTextArray = Arrays.asList(update.message().text().split(" "));
                    } catch (Exception e) {
                        GetUpdatedTextArray = Collections.singletonList(update.message().text());
                    }
                    if (Objects.equals(GetUpdatedTextArray.get(0), "/status")){ // Listen built-in command "status"
                        SendMessage(ChatId, (String) GetSystemStatus.Get(true),"Status",true, false); // Send system status
                    } else if (Objects.equals(GetUpdatedTextArray.get(0), "/list")){ // Listen built-in command "list"
                        SendMessage(ChatId, GetOnlinePlayers.Get(),"Status",true, false); // Send online players
                    } else if (Objects.equals(GetUpdatedTextArray.get(0), "/sudo")){ // Listen built-in command "sudo"
                        if (ChatId.equals(Configuration.OWNER_CHAT_ID())) // If sender is server owner
                        { SendCommand.Send(update.message().text()); } //Send command
                        else { telegramBot.execute(new SendMessage(update.updateId(), Messages.NOT_OWNER())); }
                    } else if (GetUpdatedTextArray.get(0).startsWith("/")){ // Send extra commands to onCommand Event
                        OnCommandEvent OnCommandEvent = new OnCommandEvent(Formatter.BotCommand(update.message().text().length(), update.message().text()));
                        Bukkit.getScheduler().runTask(this, () -> Bukkit.getServer().getPluginManager().callEvent(OnCommandEvent));
                    } else { // Send messages to GetUpdate Event
                        GetUpdateEvent GetUpdateEvent = new GetUpdateEvent(update.message().text());
                        Bukkit.getScheduler().runTask(this, () -> Bukkit.getServer().getPluginManager().callEvent(GetUpdateEvent));
                    }
                    this.getLogger().info("<" + ChatId + "|" + ChatName + "> " + update.message().text()); // Output updates message
                }
            } return UpdatesListener.CONFIRMED_UPDATES_ALL; // Markup telegram message to confirmed
        });
    }

    private void TPSListener(){
        new BukkitRunnable(){
            double[] TPS;
            @Override public void run(){
                try { TPS = GetTPS.Get(); }
                catch (Throwable ignored) { }
                if(TPS[0] != 0.0){
                    if(TPS[0] > Configuration.TPS_MAX_THRESHOLD()){
                        SendMessage(
                                Configuration.OWNER_CHAT_ID(),
                                Formatter.PluginVariable(Messages.TPS_TOO_HIGH(), "TPS", TPS[0]
                                ), "Warn", true, false
                        );
                    } else if (TPS[0] < Configuration.TPS_MIN_THRESHOLD()){
                        SendMessage(
                                Configuration.OWNER_CHAT_ID(),
                                Formatter.PluginVariable(Messages.TPS_TOO_LOW(), "TPS", TPS[0]
                                ), "Warn", true, false
                        );
                    }
                }
            }
        }.runTaskTimer(this, 0, 20L * Configuration.TPS_CHECK_TIMEOUT());
    }

    @EventHandler private void playerLogin(PlayerLoginEvent event){
        if (Configuration.ENABLE_LOGIN_LOGOUT_MSG()){
            SendMessage(
                    Configuration.OWNER_CHAT_ID(),
                    Formatter.PluginVariable(Messages.PLAYER_LOGIN(), "player", event.getPlayer().getName()
                    ), "Info", true, false
            );
        }
    }
    @EventHandler private void playerLogout(PlayerQuitEvent event){
        if (Configuration.ENABLE_LOGIN_LOGOUT_MSG()){
            SendMessage(
                    Configuration.OWNER_CHAT_ID(),
                    Formatter.PluginVariable(Messages.PLAYER_LOGOUT(), "player", event.getPlayer().getName()
                    ), "Info", true, false
            );
        }
    }

    protected void SendMessage(String SendTo, String Msg, String MsgType, boolean FormatMsg ,boolean syncMsg){
        if(FormatMsg){
            Calendar cal=Calendar.getInstance(); // Add timestamp to message
            String Message;
            String time = cal.get(Calendar.HOUR_OF_DAY) + " : " + cal.get(Calendar.MINUTE) + " : " + cal.get(Calendar.SECOND) + "\n";
            switch (MsgType){ // Add message type string to message
                case "Status" -> Message = Characters.STATUS_ICON + " [Status] " + time + Msg;
                case "Warn" -> Message = Characters.WARING_ICON + " [Warn] " + time + Msg;
                case "Info" -> Message = Characters.INFO_ICON + " [Info] " + time + Msg;
                default -> Message = time + Msg;
            }
            telegramBot.execute(new SendMessage(SendTo, Message)); // Send message to Owner
            if (syncMsg) SyncMsg(Message); // Send message to other chats
        } else {
            telegramBot.execute(new SendMessage(SendTo, Msg)); // Directly send message to Owner
            if (syncMsg) SyncMsg(Msg); // Directly send message to other chats
        }
    }
    private void SyncMsg(String message) {
        for (String s : Configuration.SYNC_MSG_TO()) {
            telegramBot.execute(new SendMessage(s, message));
        }
    }
}
