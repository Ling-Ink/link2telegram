<h1 align="center">
    <img width="500" src="https://raw.githubusercontent.com/Crystal-Moling/link2telegram/master/Banner.png"/><br>
    Link2Telegram
</h1>
<p align="center">
    English | <a href="README-ZH.md">简体中文</a>
</p>

# introduce
Connect to Telegram Bot via plugin

# load
* Download the latest version of the jar package from the [Release](https://github.com/Crystal-Moling/link2telegram/releases/latest) interface
* Copy the jar package to the project root directory
* ##maven
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
* ##Gradle
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
[send message](#Send message)  
[Get message](#Get message)  
[TPS MONITORING](#TPS monitoring)  
[Get server basic data](#Get server basic data)  
[config.yml](#config)  
[Bot Command](#BotCommands)
## Send message
Send a message using a bot
  ````java
  Link2telegram.L2tAPI().sendMsg("<message to be sent>");
  ````
Note: This method does not format the message to be sent, if you want to format, use the following code:
  ````java
  Link2telegram.L2tAPI().sendFormattedMsg("<message to be sent>", "<message type>");
  ````
Currently supported message types are Status/Warn/Info, other characters will be formatted as unprefixed information
## Get message
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
## TPS monitoring
This method can send a warning message when the server TPS exceeds or falls below a set threshold  
The set threshold can be modified in config.yml
### Get TPS
  ````java
  Link2telegram.L2tAPI().getServerTPS();
  ````
Return value type: double[]
## Get server basic data
This method can return the current CPU and memory usage of the server
  ````java
  Link2telegram.L2tAPI().getServerStatus();
  ````
Return value type: int[CPU usage, memory usage]  
This method can also use the built-in Bot command `/status` to get the status  
See [BotCommands](#BotCommands)
##config
````
# BotToken obtained from @BotFather
BotToken: 123456:qwertyuiopASDFGHJKL
# Send information to this chatId
SendMsgToChatID: 1234567890
Proxy:
  #Proxy server address, if not filled, no proxy will be used
  Hostname: 127.0.0.1
  # proxy server port
  Port: 7890
DefaultMsg:
  # The message sent when the server starts
  PluginOnEnableMsg: 'Server started'
  # The message sent when the server shuts down
  PluginOnDisableMsg: 'Server down'

# Plugin function
TPSMonitor:
  # Whether to enable TPS monitoring
  Enabled: true
  # TPS monitoring interval, in seconds
  TPSCheckTimeout: 5
  # TPS max threshold
  MaxTPSThreshold: 22
  # TPS high warning message
  TPSTooHighInformation: 'TPS is too high, current TPS:'
  # Whether to add the current TPS after the warning message
  THIEndedWithTPS: true
  # TPS minimum threshold
  MinTPSThreshold: 18
  # TPS low warning message
  TPSTooLowInformation: 'TPS is too low, current TPS:'
  # Whether to add the current TPS after the warning message
  TLIEndedWithTPS: true
````
## BotCommands
The default built-in commands of the plugin are listed here, and these commands cannot be monitored by `OnCommandEvent`
### /status
Get the basic information of the current server and return the message format:
````
ℹ️[Info] 12:34:56
CPU: 10%
Memory: 45%
````
### /sudo
for executing commands  
Example: To execute the command `/say test`, send the command `/sudo say test`
# dependencies
[okhttp](https://github.com/square/okhttp)  
[java-telegram-bot-api](https://github.com/pengrad/java-telegram-bot-api)

# License
MIT