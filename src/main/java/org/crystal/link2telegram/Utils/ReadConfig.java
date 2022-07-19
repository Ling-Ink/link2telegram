package org.crystal.link2telegram.Utils;

import org.crystal.link2telegram.Link2telegram;
import org.crystal.link2telegram.plugin.Configuration;
import org.crystal.link2telegram.plugin.Messages;

public class ReadConfig {
    private static Link2telegram L2t;
    public ReadConfig(Link2telegram l2t){ L2t = l2t; }

    public static void Read(){
        Messages.PLUGIN_ON_ENABLE(L2t.getStringConfig("Messages.PluginOnEnableMsg"));
        Messages.PLUGIN_ON_DISABLE(L2t.getStringConfig("Messages.PluginOnDisableMsg"));
        Messages.TPS_TOO_HIGH(L2t.getStringConfig("Messages.TPSTooHighMsg"));
        Messages.TPS_TOO_LOW(L2t.getStringConfig("Messages.TPSTooLowMsg"));
        Messages.PLAYER_LOGIN(L2t.getStringConfig("Messages.PlayerLoginMsg"));
        Messages.PLAYER_LOGOUT(L2t.getStringConfig("Messages.PlayerLogoutMsg"));
        Messages.NOT_OWNER(L2t.getStringConfig("Messages.NotOwnerCommand"));
        Configuration.BOT_TOKEN(L2t.getStringConfig("BotToken"));
        Configuration.OWNER_CHAT_ID(L2t.getStringConfig("OwnerChatId"));
        Configuration.SYNC_MSG_TO(Formatter.ToSyncList(L2t.getStringListConfig("SendMsgToChatID")));
        Configuration.PROXY_HOSTNAME(L2t.getStringConfig("Proxy.Hostname"));
        Configuration.PROXY_PORT(L2t.getIntegerConfig("Proxy.Port"));
        Configuration.ENABLE_START_STOP_MSG(L2t.getBooleanConfig("ServerStart/StopMessage.Enabled"));
        Configuration.ENABLE_TPS_MONITOR(L2t.getBooleanConfig("TPSMonitor.Enabled"));
        Configuration.TPS_CHECK_TIMEOUT(L2t.getIntegerConfig("TPSMonitor.TPSCheckTimeout"));
        Configuration.TPS_MAX_THRESHOLD(L2t.getIntegerConfig("TPSMonitor.MaxTPSThreshold"));
        Configuration.TPS_MIN_THRESHOLD(L2t.getIntegerConfig("TPSMonitor.MinTPSThreshold"));
        Configuration.ENABLE_LOGIN_LOGOUT_MSG(L2t.getBooleanConfig("PlayerLoginLogout.Enabled"));
    }
}
