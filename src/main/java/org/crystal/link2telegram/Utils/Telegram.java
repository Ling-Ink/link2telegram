package org.crystal.link2telegram.Utils;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import okhttp3.OkHttpClient;
import org.crystal.link2telegram.Events.GetUpdateEvent;
import org.crystal.link2telegram.Events.OnCommandEvent;
import org.crystal.link2telegram.Link2telegram;
import org.crystal.link2telegram.plugin.Characters;
import org.crystal.link2telegram.plugin.Configuration;
import org.crystal.link2telegram.plugin.Messages;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Telegram {
    private static Link2telegram L2t;
    public Telegram(Link2telegram l2t){ L2t = l2t; }

    private static TelegramBot telegramBot;

    public static void Initialize(){ // Set proxy and bot
        if (Configuration.PROXY_HOSTNAME() != null){
            OkHttpClient client = new OkHttpClient.Builder()
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                            Configuration.PROXY_HOSTNAME(),
                            Configuration.PROXY_PORT())))
                    .build();
            telegramBot = new TelegramBot.Builder(Configuration.BOT_TOKEN()).okHttpClient(client).build();
        } else {
            telegramBot = new TelegramBot(Configuration.BOT_TOKEN());
        }
        ListenUpdateText();
        if (Configuration.ENABLE_TPS_MONITOR()) {
            L2t.TPSListener();
        }
    }

    private static void ListenUpdateText(){
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
                    if (GetUpdatedTextArray.get(0).startsWith("/")){ // Send extra commands to onCommand Event
                        OnCommandEvent OnCommandEvent = new OnCommandEvent(Formatter.BotCommand(update.message().text().length(), update.message().text()), ChatId);
                        L2t.CallBukkitEvent(OnCommandEvent);
                    } else { // Send messages to GetUpdate Event
                        GetUpdateEvent GetUpdateEvent = new GetUpdateEvent(update.message().text());
                        L2t.CallBukkitEvent(GetUpdateEvent);
                    }
                    L2t.sendLogInfo("<" + ChatId + "|" + ChatName + "> " + update.message().text()); // Output updates message
                }
            } return UpdatesListener.CONFIRMED_UPDATES_ALL; // Markup telegram message to confirmed
        });
    }

    public static void TPSListener(){
        double[] TPS = new double[0];
        try { TPS = GetTPS.Get(); }
        catch (Throwable ignored) { }
        if(TPS[0] != 0.0){
            if(TPS[0] > Configuration.TPS_MAX_THRESHOLD()){
                Telegram.SendMessage(
                        Configuration.OWNER_CHAT_ID(),
                        Formatter.PluginVariable(Messages.TPS_TOO_HIGH(), "TPS", TPS[0]
                        ), "Warn", true, false
                );
            } else if (TPS[0] < Configuration.TPS_MIN_THRESHOLD()){
                Telegram.SendMessage(
                        Configuration.OWNER_CHAT_ID(),
                        Formatter.PluginVariable(Messages.TPS_TOO_LOW(), "TPS", TPS[0]
                        ), "Warn", true, false
                );
            }
        }
    }

    public static void SendMessage(String SendTo, String Msg, String MsgType, boolean FormatMsg, boolean syncMsg){
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
    private static void SyncMsg(String message) {
        for (String s : Configuration.SYNC_MSG_TO()) {
            telegramBot.execute(new SendMessage(s, message));
        }
    }
}
