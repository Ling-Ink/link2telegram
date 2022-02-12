package org.crystal.link2telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import okhttp3.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;
import com.sun.management.OperatingSystemMXBean;
import org.crystal.link2telegram.Events.GetUpdateEvent;
import org.crystal.link2telegram.Events.OnCommandEvent;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Calendar;
import java.util.Objects;

public class Link2telegram extends JavaPlugin {
    private static Link2telegramAPI L2tAPI;
    public static Link2telegramAPI L2tAPI(){
        return L2tAPI;
    }
    private boolean GetBooleanConfig(String path){ return this.getConfig().getBoolean(path);}
    private String GetStringConfig(String path){
        return this.getConfig().getString(path);
    }
    private int GetIntConfig(String path){
        return this.getConfig().getInt(path);
    }

    private static Object minecraftServer;
    private static Field recentTps;
    protected String UpdateText;
    private TelegramBot bot;

    @Override public void onEnable() {
        L2tAPI = new Link2telegramAPI(this);
        this.saveDefaultConfig();
        InitializeBot();
        ListenUpdateText();
        if(this.getConfig().getBoolean("TPSMonitor.Enabled")){ TPSListener(); }
        onEnableMsg();
        SendMessage(this.getConfig().getString("DefaultMsg.PluginOnEnableMsg"),"Status",true);
    }
    @Override public void onDisable() {
        this.getLogger().info("Plugin Disabled!");
        SendMessage(this.getConfig().getString("DefaultMsg.PluginOnDisableMsg"),"Status",true);
    }
    private void onEnableMsg(){
        this.getLogger().info("#######################");
        this.getLogger().info("#    Link2telegram    #");
        this.getLogger().info("#     Version 1.1     #");
        this.getLogger().info("#######################");
    }

    private void InitializeBot(){
        String ProxyHostname = this.getConfig().getString("Proxy.Hostname");
        int ProxyPort = this.getConfig().getInt("Proxy.Port");
        if(ProxyHostname != null){
            OkHttpClient client = new OkHttpClient.Builder()
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ProxyHostname, ProxyPort)))
                    .addInterceptor(new OkHttpInterceptor())
                    .build();
            bot = new TelegramBot.Builder(this.getConfig().getString("BotToken")).okHttpClient(client).build();
        } else { bot = new TelegramBot(this.getConfig().getString("BotToken")); }
    }

    protected void SendMessage(String Msg, String MsgType, boolean FormatMsg){
        if(FormatMsg){
            Calendar cal=Calendar.getInstance();
            String STATUS_ICON = "\uD83D\uDCCA";
            String WARING_ICON = "⚠️";
            String INFO_ICON = "ℹ️";
            String Message;
            String time = cal.get(Calendar.HOUR_OF_DAY) + " : " + cal.get(Calendar.MINUTE) + " : " + cal.get(Calendar.SECOND) + "\n";
            switch (MsgType){
                case "Status" -> Message = STATUS_ICON + " [Status] " + time + Msg;
                case "Warn" -> Message = WARING_ICON + " [Warn] " + time + Msg;
                case "Info" -> Message = INFO_ICON + " [Info] " + time + Msg;
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
                        SendMessage((String) GetSystemStatus(true),"Info",true);
                    } else if(Objects.equals(GetUpdatedTextArray[0], "/sudo")){
                        sudo(update.message().text());
                    } else if (GetUpdatedTextArray[0].startsWith("/")){
                        StringBuilder Command = new StringBuilder();
                        for (int i = 1; i < update.message().text().length(); i++) {  Command.append(update.message().text().charAt(i)); }
                        OnCommandEvent OnCommandEvent = new OnCommandEvent(Command.toString());
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

    protected double[] getRecentTpsReflector() throws Throwable {
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
            double[] TPS;
            @Override
            public void run(){
                try { TPS = getRecentTpsReflector(); }
                catch (Throwable ignored) { }
                if(TPS[0] > GetIntConfig("TPSMonitor.MaxTPSThreshold")){
                    if(GetBooleanConfig("TPSMonitor.THIEndedWithTPS"))
                    { SendMessage(GetStringConfig("TPSMonitor.TPSTooHighInformation") + TPS[0],"Warn",true); }
                    else{ SendMessage(GetStringConfig("TPSMonitor.TPSTooHighInformation"),"Warn",true); }
                } else if (TPS[0] < GetIntConfig("TPSMonitor.MinTPSThreshold")){
                    if(GetBooleanConfig("TPSMonitor.TLIEndedWithTPS"))
                    { SendMessage(GetStringConfig("TPSMonitor.TPSTooLowInformation") + TPS[0],"Warn",true); }
                    else{ SendMessage(GetStringConfig("TPSMonitor.TPSTooLowInformation"),"Warn",true); }
                }
            }
        }.runTaskTimer(this,0,20L * this.getConfig().getInt("TPSMonitor.TPSCheckTimeout"));
    }

    protected Object GetSystemStatus(boolean Format){
        OperatingSystemMXBean OSMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        int MemoryLoad;
        int CPULoad;
        double cpu = OSMXBean.getProcessCpuLoad();
        CPULoad = (int) (cpu * 100);
        double totalVirtualMemory = OSMXBean.getTotalMemorySize();
        double freePhysicalMemorySize = OSMXBean.getFreeMemorySize();
        double value = freePhysicalMemorySize / totalVirtualMemory;
        MemoryLoad =  (int) ((1 - value) * 100);
        if(Format){
            return "CPU:" + CPULoad + "%\n" +
                   "Memory:" + MemoryLoad + "%";
        } else {
            return new int[]{CPULoad,
                    MemoryLoad};
        }
    }

    private void sudo(String Command){
        StringBuilder SBCommand = new StringBuilder();
        for (int i = 1; i < Command.length(); i++) { SBCommand.append(Command.charAt(i)); }
        String[] CommandArray = SBCommand.toString().split(" ");
        StringBuilder OriginalCommand = new StringBuilder();
        for (int j =1; j < CommandArray.length; j++){ OriginalCommand.append(CommandArray[j]).append(" "); }
        Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),OriginalCommand.toString()));
    }
}
