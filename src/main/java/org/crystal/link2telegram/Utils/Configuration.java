package org.crystal.link2telegram.Utils;

import java.util.List;

public class Configuration {
    private static String BOT_TOKEN;
    private static String OWNER_CHAT_ID;
    private static List<String> SEND_MSG_TO;
    private static String PROXY_HOSTNAME;
    private static int PROXY_PORT;
    private static boolean ENABLE_START_STOP_MSG;
    private static boolean ENABLE_TPS_MONITOR;
    private static int TPS_CHECK_TIMEOUT;
    private static int TPS_MAX_THRESHOLD;
    private static int TPS_MIN_THRESHOLD;
    private static boolean ENABLE_LOGIN_MSG;

    public static void BOT_TOKEN(String string){ BOT_TOKEN = string; }
    public static String BOT_TOKEN(){ return BOT_TOKEN; }
    public static void OWNER_CHAT_ID(String string){  OWNER_CHAT_ID = string; }
    public static String OWNER_CHAT_ID(){ return OWNER_CHAT_ID; }
    public static void SEND_MSG_TO(List<String> listString){  SEND_MSG_TO = listString; }
    public static List<String> SEND_MSG_TO(){ return SEND_MSG_TO; }
    public static void PROXY_HOSTNAME(String string){  PROXY_HOSTNAME = string; }
    public static String PROXY_HOSTNAME(){ return PROXY_HOSTNAME; }
    public static void PROXY_PORT(int integer){  PROXY_PORT = integer; }
    public static int PROXY_PORT(){ return PROXY_PORT; }
    public static void ENABLE_START_STOP_MSG(boolean bool){  ENABLE_START_STOP_MSG = bool; }
    public static boolean ENABLE_START_STOP_MSG(){ return ENABLE_START_STOP_MSG; }
    public static void ENABLE_TPS_MONITOR(boolean bool){  ENABLE_TPS_MONITOR = bool; }
    public static boolean ENABLE_TPS_MONITOR(){ return ENABLE_TPS_MONITOR; }
    public static void TPS_CHECK_TIMEOUT(int integer){  TPS_CHECK_TIMEOUT = integer; }
    public static int TPS_CHECK_TIMEOUT(){ return TPS_CHECK_TIMEOUT; }
    public static void TPS_MAX_THRESHOLD(int integer){  TPS_MAX_THRESHOLD = integer; }
    public static int TPS_MAX_THRESHOLD(){ return TPS_MAX_THRESHOLD; }
    public static void TPS_MIN_THRESHOLE(int integer){  TPS_MIN_THRESHOLD = integer; }
    public static int TPS_MIN_THRESHOLE(){ return TPS_MIN_THRESHOLD; }
    public static void ENABLE_LOGIN_MSG(boolean bool){  ENABLE_LOGIN_MSG = bool; }
    public static boolean ENABLE_LOGIN_MSG(){ return ENABLE_LOGIN_MSG; }
}
