package org.crystal.link2telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import okhttp3.OkHttpClient;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;
import com.sun.management.OperatingSystemMXBean;

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

    private static Object minecraftServer;
    private static Field recentTps;
    protected String UpdateText;
    private TelegramBot bot;

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
                    GetUpdateEvent GetUpdateEvent = new GetUpdateEvent(update.message().text());
                    Bukkit.getScheduler().runTask(this, () -> Bukkit.getServer().getPluginManager().callEvent(GetUpdateEvent));
                    this.getLogger().info(update.message().text());
                    if(Objects.equals(update.message().text(), "/status")){
                        SendMessage((String) GetSystemStatus(true),"Info",true);
                    }
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
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
                case "Status" -> Message = STATUS_ICON + " [状态] " + time + Msg;
                case "Warn" -> Message = WARING_ICON + " [警告] " + time + Msg;
                case "Info" -> Message = INFO_ICON + " [信息] " + time + Msg;
                default -> Message = time + Msg;
            }
            bot.execute(new SendMessage(this.getConfig().getString("SendMsgToChatID"), Message));
        } else {
            bot.execute(new SendMessage(this.getConfig().getString("SendMsgToChatID"), Msg));
        }
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

    protected Object GetSystemStatus(boolean Format){
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        int MemoryLoad;
        int CPULoad;
        double cpu = osmxb.getProcessCpuLoad();
        CPULoad = (int) (cpu * 100);
        double totalvirtualMemory = osmxb.getTotalMemorySize();
        double freePhysicalMemorySize = osmxb.getFreeMemorySize();
        double value = freePhysicalMemorySize / totalvirtualMemory;
        MemoryLoad =  (int) ((1 - value) * 100);
        if(Format){
            return "CPU:" + CPULoad + "%\n" +
                   "Memory:" + MemoryLoad + "%";
        } else {
            return new int[]{CPULoad,
                    MemoryLoad};
        }
    }
}
