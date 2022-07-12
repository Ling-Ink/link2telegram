package org.crystal.link2telegram.plugin;

public class Messages {
    private static String PLUGIN_ON_ENABLE;
    private static String PLUGIN_ON_DISABLE;
    private static String TPS_TOO_HIGH;
    private static String TPS_TOO_LOW;
    private static String PLAYER_LOGIN;

    public static void PLUGIN_ON_ENABLE(String String){ PLUGIN_ON_ENABLE = String; }
    public static String PLUGIN_ON_ENABLE(){ return PLUGIN_ON_ENABLE; }
    public static void PLUGIN_ON_DISABLE(String String){ PLUGIN_ON_DISABLE = String; }
    public static String PLUGIN_ON_DISABLE(){ return PLUGIN_ON_DISABLE; }
    public static void TPS_TOO_HIGH(String String){ TPS_TOO_HIGH = String; }
    public static String TPS_TOO_HIGH(){ return TPS_TOO_HIGH ; }
    public static void TPS_TOO_LOW(String String){ TPS_TOO_LOW = String; }
    public static String TPS_TOO_LOW(){ return TPS_TOO_LOW; }
    public static void PLAYER_LOGIN(String String){ PLAYER_LOGIN = String; }
    public static String PLAYER_LOGIN(){ return PLAYER_LOGIN; }
}
