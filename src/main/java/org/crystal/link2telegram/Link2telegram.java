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
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;
import com.sun.management.OperatingSystemMXBean;
import org.crystal.link2telegram.Events.GetUpdateEvent;
import org.crystal.link2telegram.Events.OnCommandEvent;
import org.crystal.link2telegram.Utils.OkHttpInterceptor;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Calendar;
import java.util.Objects;

public class Link2telegram extends JavaPlugin implements Listener {
    private static Link2telegramAPI L2tAPI;
    public static Link2telegramAPI L2tAPI(){ return L2tAPI; }
    private String GetStringConfig(String path){ return this.getConfig().getString(path); }
    private int GetIntConfig(String path){ return this.getConfig().getInt(path); }

    private static Object minecraftServer;
    private static Field recentTps;
    protected String UpdateText;
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
    private void onEnableMsg(){
        this.getLogger().info("#     Link2telegram     #");
        this.getLogger().info("#   Version 1.1.1-pre   #");
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("basic")) { //如果玩家输入了/basic则执行如下内容...
            if ((sender instanceof Player)) { //如果sender与Player类不匹配
                InitializeBotAbout();
            } else {
                sender.sendMessage("这个指令只能让玩家使用。");
            }
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

    @EventHandler private void playerLogin(PlayerLoginEvent event){
        SendMessage(
                FormatByPluginVariable(
                        GetStringConfig("PlayerLogin.PlayerLoginMessage"),
                        "player",
                        event.getPlayer().getName()
                ),
                "Info",
                true
        );
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
                    SendMessage(
                            FormatByPluginVariable(
                                    GetStringConfig("TPSMonitor.TPSTooHighInformation"),
                                    "TPS",
                                    TPS[0]
                            ),
                            "Warn",
                            true
                    );
                } else if (TPS[0] < GetIntConfig("TPSMonitor.MinTPSThreshold")){
                    SendMessage(
                            FormatByPluginVariable(
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

    private String FormatByPluginVariable(String configText, String VariableName, Object Variable){
        String[] GetText = configText.split("%");
        StringBuilder OutputString = new StringBuilder();
        for (String s : GetText) {
            if (!s.equals(VariableName)) { OutputString.append(s); }
            else { OutputString.append(Variable); }
        }
        return OutputString.toString();
    }
}
