<h1 align="center">
    <img width="500" src="https://raw.githubusercontent.com/Crystal-Moling/link2telegram/master/Banner.png"/><br>
    Link2Telegram
</h1>
<p align="center">
    English | <a href="README-ZH.md">简体中文</a>
</p>

# Introduce
Connect to Telegram Bot via plugin

# Development
* Download the latest version of the jar package from the [Release](https://github.com/Crystal-Moling/link2telegram/releases/latest) interface
* Copy the jar package to the project root directory
* ## maven
  * Add the following to pom.xml
    ````
      <dependency>
        <groupId>org.crystal.link2telegram</groupId>
        <artifactId>link2telegram</artifactId>
        <version>1.0</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/link2telegram-1.0.jar</systemPath>
      </dependency>
    ````
* ## Gradle
  * Add the following to build.gradle
    ````
      dependencies {
        compile files('link2telegram-1.0.jar')
      }
    ````
* Add a reference to the project
  ````java
  import org.crystal.link2telegram.Link2telegram;
  ````
# Documentation
[Send Message](#SendMessage)  
[Get Message](#GetMessage)  
[TPS Monitoring](#TPSMonitoring)  
[Get Server Basic Data](#GetServerBasicData)  
[config.yml](#config)  
[Bot Command](#BotCommands)
## SendMessage
Send a message using a bot
  ````java
  Link2telegram.L2tAPI().sendFormattedMsg("<message to be sent>", ["<message type>"]);
  ````
Currently supported message types are Status/Warn/Info, other characters will be formatted as unprefixed information
## GetMessage
Get the last message received by the bot
### Instructions
* Add `implements Listener` to the main plugin class
* Register the listener in `onEnable`
  ````java
  getServer().getPluginManager().registerEvents(this, this);
  ````
* Use the following code to monitor the get message event
  ````java
  @EventHandler(priority = EventPriority.MONITOR)
  private void GetUpdateListener(GetUpdateEvent event){
  }
  ````
* Use `event.GetMessage()` to get the received message

Return value type: String
## get command
Get the command obtained by the robot (starting with "/")
### Instructions
* Add `implements Listener` to the main plugin class
* Register the listener in `onEnable`
  ````java
  getServer().getPluginManager().registerEvents(this, this);
  ````
* Use the following code to listen for command events
  ````java
  @EventHandler(priority = EventPriority.MONITOR)
  private void GetCommandListener(OnCommandEvent event){
  }
  ````
* Use `event.GetCommand()` to get the received command text (without "/")

`/status and /sudo are built-in commands and cannot be monitored. For details, see `[BotCommands](#BotCommands)  
Return value type: String[]
## TPSMonitoring
This method can send a warning message when the server TPS exceeds or falls below a set threshold  
The set threshold can be modified in config.yml
### Get TPS
  ````java
  Link2telegram.L2tAPI().getServerTPS();
  ````
Return value type: double[]
## GetServerBasicData
This method can return the current CPU and memory usage of the server
  ````java
  Link2telegram.L2tAPI().getServerStatus();
  ````
Return value type: Object[OS Type, CPU Usage, Memory Usage, Used Disk Space, Total Disk Space]  
This method can also use the built-in Bot command `/status` to get the status  
See [BotCommands](#BotCommands)
## config
### Plugin Variable
`%TPS%`Server TPS  
`%player%`Logged in player
### Plugin Configuration
````
# BotToken obtained from @BotFather
BotToken: '123456:qwertyuiopASDFGHJKL'
# Send information to this chatId
SendMsgToChatID: 1234567890
Proxy:
  #Proxy server address, if not filled, no proxy will be used
  Hostname: '127.0.0.1'
  # proxy server port
  Port: 7890

# Plugin function
ServerStart/StopMessage:
  # Whether to enable Server Start/Stop Message
  Enabled: true
  # The message sent when the server starts
  PluginOnEnableMsg: 'Server started'
  # The message sent when the server shuts down
  PluginOnDisableMsg: 'Server down'
TPSMonitor:
  # Whether to enable TPS monitor
  Enabled: true
  # TPS monitoring interval, in seconds
  TPSCheckTimeout: 5
  # TPS max threshold
  MaxTPSThreshold: 22
  # TPS high warning message
  TPSTooHighInformation: 'TPS is too high, current TPS:%TPS%'
  # TPS minimum threshold
  MinTPSThreshold: 18
  # TPS low warning message
  TPSTooLowInformation: 'TPS is too low, current TPS:%TPS%'
PlayerLogin:
  # Whether to enable Player login Listener
  Enabled: true
  # Player Login Message
  PlayerLoginMessage: 'Player %player% login!'
````
## BotCommands
The default built-in commands of the plugin are listed here, and these commands cannot be monitored by `OnCommandEvent`
### /status
Get the basic information of the current server and return the message format:
````
ℹ️[Info] 12:34:56
CPU: [█░░░░░░░░░]10%
Memory: [████▌░░░░░]45%
Disk:
   Root Path:/
   Used Disk:14G / 49G
````
### /sudo
for executing commands  
Example: To execute the command `/say test`, send the command `/sudo say test`
# dependencies
[okhttp](https://github.com/square/okhttp)  
[java-telegram-bot-api](https://github.com/pengrad/java-telegram-bot-api)

# License
MIT