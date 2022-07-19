package org.crystal.link2telegram.Utils;

import org.crystal.link2telegram.Link2telegram;
import org.crystal.link2telegram.plugin.Configuration;

public class ConfigUitls {
    private static Link2telegram L2t;
    public ConfigUitls(Link2telegram l2t){ L2t = l2t; }
    private static String[] ConfigPathList = {
            "ServerStart/StopMessage.Enabled",
            "TPSMonitor.Enabled",
            "TPSMonitor.TPSCheckTimeout",
            "TPSMonitor.MaxTPSThreshold",
            "TPSMonitor.MinTPSThreshold",
            "PlayerLoginLogout.Enabled"
    };
    public static String Get(){
        return "reply /setconfig <index> <value> to change config" + "\n" +
                "[0]Enable Start Stop Msg : " + Configuration.ENABLE_START_STOP_MSG() + "\n" +
                "[1]Enable TPS Monitor : " + Configuration.ENABLE_TPS_MONITOR() + "\n" +
                "[2]TPS Check Timeout : " + Configuration.TPS_CHECK_TIMEOUT() + "\n" +
                "[3]TPS Max Threshold : " + Configuration.TPS_MAX_THRESHOLD() + "\n" +
                "[4]TPS Min Threshold : " + Configuration.TPS_MIN_THRESHOLD() + "\n" +
                "[5]Enable Login Logout Msg : " + Configuration.ENABLE_LOGIN_LOGOUT_MSG();
    }
    public static String Set(Integer index, Object value){
        String returnStr = null;
        try {
            if (index == 0 || index == 1 || index == 5){
                L2t.setConfig(ConfigPathList[index], Boolean.parseBoolean((String)value));
                returnStr = "Succeed to set config index [" + index + "] to value [" + Boolean.parseBoolean((String)value) + "]";
            } else if (index == 2 || index == 3 || index == 4){
                L2t.setConfig(ConfigPathList[index], Integer.parseInt((String)value));
                returnStr = "Succeed to set config index [" + index + "] to value [" + Integer.parseInt((String)value) + "]";
            }
            L2t.reloadConf();
        } catch (Exception e) {
            returnStr = "Unable to set config : " + e.getMessage();
        }
        return returnStr;
    }
}
