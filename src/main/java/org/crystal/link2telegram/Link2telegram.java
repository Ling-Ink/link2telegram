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
    private void onEnableMsg(){ // Startup message
        String[] SyncMsgTo = Configuration.SYNC_MSG_TO().toArray(new String[0]);
        StringBuilder SyncMsgToString = new StringBuilder();
        for (String s : SyncMsgTo) {
            try {
                String[] ConfigArr = s.split(":");
                if (ConfigArr.length == 2) {
                    if (ConfigArr[0].equals("AT")) {
                        SyncMsgToString.append("@").append(ConfigArr[1]).append(" ");
                    }
                } else if (ConfigArr.length == 1){
                    SyncMsgToString.append(s).append(" ");
                }
            } catch (Exception ignored) { }
        }
        this.getLogger().info("#     Link2telegram     #");
        this.getLogger().info("#     Version 1.3.1     #");
        this.getLogger().info("# Plugin owner ID: " + Configuration.OWNER_CHAT_ID());
        this.getLogger().info("# Message send to: " + SyncMsgToString);
    }

    private TelegramBot bot;

    private void ReadMsgConfig(){
        Messages.PLUGIN_ON_ENABLE(this.getConfig().getString("Messages.PluginOnEnableMsg"));
        Messages.PLUGIN_ON_DISABLE(this.getConfig().getString("Messages.PluginOnDisableMsg"));
        Messages.TPS_TOO_HIGH(this.getConfig().getString("Messages.TPSTooHighMsg"));
        Messages.TPS_TOO_LOW(this.getConfig().getString("Messages.TPSTooLowMsg"));
        Messages.PLAYER_LOGIN(this.getConfig().getString("Messages.PlayerLoginMsg"));
        Messages.NOT_OWNER(this.getConfig().getString("Messages.NotOwnerCommand"));
        Configuration.BOT_TOKEN(this.getConfig().getString("BotToken"));
        Configuration.OWNER_CHAT_ID(this.getConfig().getString("OwnerChatId"));
        Configuration.SYNC_MSG_TO(this.getConfig().getStringList("SendMsgToChatID"));
        Configuration.PROXY_HOSTNAME(this.getConfig().getString("Proxy.Hostname"));
        Configuration.PROXY_PORT(this.getConfig().getInt("Proxy.Port"));
        Configuration.ENABLE_START_STOP_MSG(this.getConfig().getBoolean("ServerStart/StopMessage.Enabled"));
        Configuration.ENABLE_TPS_MONITOR(this.getConfig().getBoolean("TPSMonitor.Enabled"));
        Configuration.TPS_CHECK_TIMEOUT(this.getConfig().getInt("TPSMonitor.TPSCheckTimeout"));
        Configuration.TPS_MAX_THRESHOLD(this.getConfig().getInt("TPSMonitor.MaxTPSThreshold"));
        Configuration.TPS_MIN_THRESHOLD(this.getConfig().getInt("TPSMonitor.MinTPSThreshold"));
        Configuration.ENABLE_LOGIN_MSG(this.getConfig().getBoolean("PlayerLogin.Enabled"));

    }

    @Override public void onEnable() { // When plugin enabled, get and send enabled massage from config.yml
        Metrics metrics = new Metrics(this, 14304);
        L2tAPI = new Link2telegramAPI(this);
        this.saveDefaultConfig();
        ReadMsgConfig();
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
        if (cmd.getName().equalsIgnoreCase("l2treload")) {
            if (!(sender instanceof Player)) {
                this.getLogger().info("Reload Configurations");
                ReadMsgConfig();
                onEnableMsg();
            }
            else { sender.sendMessage("This command can only be used in console."); }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("l2treinit")) {
            if (!(sender instanceof Player)) {
                this.getLogger().info("Reinitialize Bot");
                InitializeBot();
            }
            else { sender.sendMessage("This command can only be used in console."); }
            return true;
        }
        return false;
    }

    private void InitializeBot(){ // Set proxy and bot
        if(Configuration.PROXY_HOSTNAME() != null){
            OkHttpClient client = new OkHttpClient.Builder()
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                            Configuration.PROXY_HOSTNAME(),
                            Configuration.PROXY_PORT())))
                    .build();
            bot = new TelegramBot.Builder(Configuration.BOT_TOKEN()).okHttpClient(client).build();
        } else { bot = new TelegramBot(Configuration.BOT_TOKEN()); }
        ListenUpdateText();
        if(Configuration.ENABLE_TPS_MONITOR())
        { TPSListener(); }
    }

    private void ListenUpdateText(){
        bot.setUpdatesListener(updates -> {
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
                        { SendBukkitCommand(update.message().text()); } //Send command
                        else { bot.execute(new SendMessage(update.updateId(), Messages.NOT_OWNER())); }
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
                                Formatter.PluginVariable(
                                        Messages.TPS_TOO_HIGH(), "TPS", TPS[0]
                                ), "Warn", true, false
                        );
                    } else if (TPS[0] < Configuration.TPS_MIN_THRESHOLD()){
                        SendMessage(
                                Configuration.OWNER_CHAT_ID(),
                                Formatter.PluginVariable(
                                        Messages.TPS_TOO_LOW(), "TPS", TPS[0]
                                ), "Warn", true, false
                        );
                    }
                }
            }
        }.runTaskTimer(this, 0, 20L * Configuration.TPS_CHECK_TIMEOUT());
    }

    @EventHandler private void playerLogin(PlayerLoginEvent event){
        if (Configuration.ENABLE_LOGIN_MSG()){
            SendMessage(
                    Configuration.OWNER_CHAT_ID(),
                    Formatter.PluginVariable(
                            Messages.PLAYER_LOGIN(), "player", event.getPlayer().getName()
                    ), "Info", true, false
            );
        }
    }

    protected void SendMessage(String SendTo, String Msg, String MsgType, boolean FormatMsg ,boolean syncMsg){
        String[] SyncMsgTo = Configuration.SYNC_MSG_TO().toArray(new String[0]);
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
            bot.execute(new SendMessage(SendTo, Message)); // Send message to Owner
            if (syncMsg) SendMsgTo(SyncMsgTo, Message); // Send message to other chats
        } else {
            bot.execute(new SendMessage(SendTo, Msg)); // Directly send message to Owner
            if (syncMsg) SendMsgTo(SyncMsgTo, Msg); // Directly send message to other chats
        }
    }
    private void SendMsgTo(String[] sendMsgTo, String message) {
        for (String s : sendMsgTo) {
            try {
                if (s.split(":")[0].equals("AT")) {
                    bot.execute(new SendMessage("@" + s.split(":")[1], message));
                }
            } catch (Exception e) {
                bot.execute(new SendMessage(s, message));
            }
        }
    }

    // Used to send commands to minecraft server
    private void SendBukkitCommand(String Command){
        StringBuilder command = new StringBuilder();
        for (int i = 1; i < Command.length(); i++) { command.append(Command.charAt(i)); }
        String[] CommandArray = command.toString().split(" "); // Split String with space
        StringBuilder OriginalCommand = new StringBuilder();
        for (int j =1; j < CommandArray.length; j++){ OriginalCommand.append(CommandArray[j]).append(" "); }
        Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),OriginalCommand.toString()));
    }
}
