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
import org.crystal.link2telegram.plugin.Configuration;
import org.crystal.link2telegram.plugin.Messages;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class Link2telegram extends JavaPlugin implements Listener {
    private static Link2telegramAPI L2tAPI;
    public static Link2telegramAPI L2tAPI(){ return L2tAPI; }
    private void onEnableMsg(){ // Startup message
        String[] SendMsgTo = Configuration.SEND_MSG_TO().toArray(new String[0]);
        StringBuilder SendMsgToString = new StringBuilder();
        for (String s : SendMsgTo) {
            try {
                String[] ConfigArr = s.split(":");
                if (ConfigArr.length == 2) {
                    if (ConfigArr[0].equals("AT")) {
                        SendMsgToString.append("@").append(ConfigArr[1]).append(" ");
                    }
                } else if (ConfigArr.length == 1){
                    SendMsgToString.append(s).append(" ");
                }
            } catch (Exception ignored) { }
        }
        this.getLogger().info("#     Link2telegram     #");
        this.getLogger().info("#     Version 1.3.1     #");
        this.getLogger().info("# Plugin owner ID: " + Configuration.OWNER_CHAT_ID());
        this.getLogger().info("# Message send to: " + SendMsgToString);
    }

    private TelegramBot bot;

    private void ReadMsgConfig(){
        Messages.PLUGIN_ON_ENABLE(this.getConfig().getString("Messages.PluginOnEnableMsg"));
        Messages.PLUGIN_ON_DISABLE(this.getConfig().getString("Messages.PluginOnDisableMsg"));
        Messages.TPS_TOO_HIGH(this.getConfig().getString("Messages.TPSTooHighMsg"));
        Messages.TPS_TOO_LOW(this.getConfig().getString("Messages.TPSTooLowMsg"));
        Messages.PLAYER_LOGIN(this.getConfig().getString("Messages.PlayerLoginMsg"));
        Configuration.BOT_TOKEN(this.getConfig().getString("BotToken"));
        Configuration.OWNER_CHAT_ID(this.getConfig().getString("OwnerChatId"));
        Configuration.SEND_MSG_TO(this.getConfig().getStringList("SendMsgToChatID"));
        Configuration.PROXY_HOSTNAME(this.getConfig().getString("Proxy.Hostname"));
        Configuration.PROXY_PORT(this.getConfig().getInt("Proxy.Port"));
        Configuration.ENABLE_START_STOP_MSG(this.getConfig().getBoolean("ServerStart/StopMessage.Enabled"));
        Configuration.ENABLE_TPS_MONITOR(this.getConfig().getBoolean("TPSMonitor.Enabled"));
        Configuration.TPS_CHECK_TIMEOUT(this.getConfig().getInt("TPSMonitor.TPSCheckTimeout"));
        Configuration.TPS_MAX_THRESHOLD(this.getConfig().getInt("TPSMonitor.MaxTPSThreshold"));
        Configuration.TPS_MIN_THRESHOLE(this.getConfig().getInt("TPSMonitor.MinTPSThreshold"));
    }

    @Override public void onEnable() { // When plugin enabled, get and send enabled massage from config.yml
        Metrics metrics = new Metrics(this, 14304);
        L2tAPI = new Link2telegramAPI(this);
        this.saveDefaultConfig();
        ReadMsgConfig();
        InitializeBot();
        if(Configuration.ENABLE_START_STOP_MSG()){
            SendMessage(Messages.PLUGIN_ON_ENABLE(),"Status",true);
        }
        getServer().getPluginManager().registerEvents(this, this);
        onEnableMsg();
    }
    @Override public void onDisable() {
        this.getLogger().info("Plugin Disabled!");
        if(Configuration.ENABLE_START_STOP_MSG()){
            SendMessage(Messages.PLUGIN_ON_DISABLE(),"Status",true);
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
                    String[] GetUpdatedTextArray = new String[0];
                    try {
                        GetUpdatedTextArray = update.message().text().split(" ");
                        if(Objects.equals(GetUpdatedTextArray[0], "/sudo")){ // Listen built-in command "sudo"
                            if (update.updateId().toString().equals(Configuration.OWNER_CHAT_ID())) // If sender is server owner
                            { SendBukkitCommand.Send(update.message().text()); } //Send command
                        } else if (GetUpdatedTextArray[0].startsWith("/")){ // Send extra commands to onCommand Event
                            OnCommandEvent OnCommandEvent = new OnCommandEvent(Formatter.BotCommand(update.message().text().length(), update.message().text()));
                            Bukkit.getScheduler().runTask(this, () -> Bukkit.getServer().getPluginManager().callEvent(OnCommandEvent));
                        }
                    } catch (Exception e) {
                        if(Objects.equals(update.message().text(), "/status")){ // Listen built-in command "status"
                            SendMessage((String) GetSystemStatus.Get(true),"Status",true); // Send system status
                        } else { // Send messages to GetUpdate Event
                            GetUpdateEvent GetUpdateEvent = new GetUpdateEvent(update.message().text());
                            Bukkit.getScheduler().runTask(this, () -> Bukkit.getServer().getPluginManager().callEvent(GetUpdateEvent));
                        }
                    }this.getLogger().info("<GetUpdate> " + update.message().text()); // Output updates message
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
                                Formatter.PluginVariable(
                                        Messages.TPS_TOO_HIGH(), "TPS", TPS[0]
                                ), "Warn", true
                        );
                    } else if (TPS[0] < Configuration.TPS_MIN_THRESHOLE()){
                        SendMessage(
                                Formatter.PluginVariable(
                                        Messages.TPS_TOO_LOW(), "TPS", TPS[0]
                                ), "Warn", true
                        );
                    }
                }
            }
        }.runTaskTimer(this, 0, 20L * Configuration.TPS_CHECK_TIMEOUT());
    }

    @EventHandler private void playerLogin(PlayerLoginEvent event){
        SendMessage(
                Formatter.PluginVariable(
                        Messages.PLAYER_LOGIN(), "player", event.getPlayer().getName()
                ), "Info", true
        );
    }

    protected void SendMessage(String Msg, String MsgType, boolean FormatMsg){
        String[] SendMsgTo = Configuration.SEND_MSG_TO().toArray(new String[0]);
        if(FormatMsg){
            Calendar cal=Calendar.getInstance(); // Add timestamp to message
            String Message;
            String time = cal.get(Calendar.HOUR_OF_DAY) + " : " + cal.get(Calendar.MINUTE) + " : " + cal.get(Calendar.SECOND) + "\n";
            switch (MsgType){ // Add msgtype string to message
                case "Status" -> Message = Characters.STATUS_ICON + " [Status] " + time + Msg;
                case "Warn" -> Message = Characters.WARING_ICON + " [Warn] " + time + Msg;
                case "Info" -> Message = Characters.INFO_ICON + " [Info] " + time + Msg;
                default -> Message = time + Msg;
            }
            // Sent message to telegram
            SendMsgTo(SendMsgTo, Message);
        } else {
            // Directly sent message to telegram
            SendMsgTo(SendMsgTo, Msg);
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
}
