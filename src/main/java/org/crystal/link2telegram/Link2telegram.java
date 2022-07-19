package org.crystal.link2telegram;

import org.bstats.bukkit.Metrics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.crystal.link2telegram.Events.OnCommandEvent;
import org.crystal.link2telegram.Utils.*;
import org.crystal.link2telegram.Utils.Formatter;
import org.crystal.link2telegram.plugin.Configuration;
import org.crystal.link2telegram.plugin.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Link2telegram extends JavaPlugin implements Listener {
    private static Link2telegramAPI L2tAPI;
    public static Link2telegramAPI L2tAPI(){ return L2tAPI; }
    private static ReadConfig ReadConf;
    public static ReadConfig ReadConf(){ return ReadConf; }
    private static SendCommand SendCmd;
    public static SendCommand SendCmd(){ return SendCmd; }
    private static Telegram Tg;
    public static Telegram Tg(){ return Tg; }
    private static ConfigUitls ConfUtils;
    public static ConfigUitls ConfUtils(){ return ConfUtils; }

    public String getStringConfig(String path){ return this.getConfig().getString(path); }
    public Boolean getBooleanConfig(String path){ return this.getConfig().getBoolean(path); }
    public Integer getIntegerConfig(String path){ return this.getConfig().getInt(path); }
    public List<String> getStringListConfig(String path){ return this.getConfig().getStringList(path); }
    public void sendLogInfo(String msg){ this.getLogger().info(msg); }
    public void setConfig(String path, Object value){
        this.getConfig().set(path, value);
        this.saveConfig();
    }
    public void reloadConf(){ this.reloadConfig(); }

    public void SendConsoleCommand(String command){
        Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command));
    }

    public void CallBukkitEvent(Event Event){
        Bukkit.getScheduler().runTask(this, () -> Bukkit.getServer().getPluginManager().callEvent(Event));
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
        Tg = new Telegram(this);
        ConfUtils = new ConfigUitls(this);
        this.saveDefaultConfig();
        ReadConfig.Read();
        Telegram.Initialize();
        if(Configuration.ENABLE_START_STOP_MSG()){
            Telegram.SendMessage(Configuration.OWNER_CHAT_ID(), Messages.PLUGIN_ON_ENABLE(),"Status",true, true);
        }
        getServer().getPluginManager().registerEvents(this, this);
        onEnableMsg();
    }
    @Override public void onDisable() {
        this.getLogger().info("Plugin Disabled!");
        if(Configuration.ENABLE_START_STOP_MSG()){
            Telegram.SendMessage(Configuration.OWNER_CHAT_ID(), Messages.PLUGIN_ON_DISABLE(),"Status",true, true);
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
                Telegram.Initialize();
                return true;
            }
        } else { sender.sendMessage("This command can only be used in console."); }
        return false;
    }

    public void TPSListener(){
        new BukkitRunnable(){
            @Override public void run(){
                Telegram.TPSListener();
            }
        }.runTaskTimer(this, 0, 20L * Configuration.TPS_CHECK_TIMEOUT());
    }

    @EventHandler private void playerLogin(PlayerLoginEvent event){
        if (Configuration.ENABLE_LOGIN_LOGOUT_MSG()){
            Telegram.SendMessage(
                    Configuration.OWNER_CHAT_ID(),
                    Formatter.PluginVariable(Messages.PLAYER_LOGIN(), "player", event.getPlayer().getName()
                    ), "Info", true, false
            );
        }
    }
    @EventHandler private void playerLogout(PlayerQuitEvent event){
        if (Configuration.ENABLE_LOGIN_LOGOUT_MSG()){
            Telegram.SendMessage(
                    Configuration.OWNER_CHAT_ID(),
                    Formatter.PluginVariable(Messages.PLAYER_LOGOUT(), "player", event.getPlayer().getName()
                    ), "Info", true, false
            );
        }
    }
    @EventHandler private void OnBotCommand(OnCommandEvent event){
        switch (event.GetCommand()[0]) {
            case "status":
                Telegram.SendMessage(event.GetChatId(), (String) GetSystemStatus.Get(true),
                        "Status",
                        true,
                        false); // Send system status
                break;
            case "list":
                Telegram.SendMessage(event.GetChatId(), GetOnlinePlayers.Get(),
                        "Status",
                        true,
                        false); // Send online players
                break;
            case "sudo":
                if (event.GetChatId().equals(Configuration.OWNER_CHAT_ID())) { // If sender is server owner
                    SendCommand.Send(event.GetCommand()); //Send command
                } else {
                    Telegram.SendMessage(event.GetChatId(), Messages.NOT_OWNER(),
                            "Warn",
                            true,
                            false);
                }
                break;
            case "config":
                if (event.GetChatId().equals(Configuration.OWNER_CHAT_ID())) { // If sender is server owner
                    Telegram.SendMessage(event.GetChatId(), ConfigUitls.Get(),
                            "Info",
                            true,
                            false);
                } else {
                    Telegram.SendMessage(event.GetChatId(), Messages.NOT_OWNER(),
                            "Warn",
                            true,
                            false);
                }
                break;
            case "setconfig":
                if (event.GetChatId().equals(Configuration.OWNER_CHAT_ID())) { // If sender is server owner
                    String[] command = event.GetCommand();
                    Telegram.SendMessage(
                            event.GetChatId(),
                            ConfigUitls.Set(
                                    Integer.parseInt(command[1]),
                                    command[2]),
                            null,
                            false,
                            false);
                } else {
                    Telegram.SendMessage(event.GetChatId(), Messages.NOT_OWNER(),
                            "Warn",
                            true,
                            false);
                }
                break;
        }
    }
}
