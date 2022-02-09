<h1 align="center">
    <img width="500" src="https://raw.githubusercontent.com/Crystal-Moling/link2telegram/master/Banner.png"/><br>
	Link2Telegram
</h1>

# Language
English | [简体中文](https://github.com/Crystal-Moling/link2telegram/blob/master/README-ZH.md)

# Description
Connect to Telegram Bot via plugin

## Load
* Copy jar package to project root directory
* Add the following to pom.xml

        <dependency>
            <groupId>org.crystal.link2telegram</groupId>
            <artifactId>link2telegram</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/link2telegram-1.0-SNAPSHOT.jar</systemPath>
        </dependency>
* Add the following to the plugin class
  ```java
  Link2telegramAPI Link2telegramAPI = new Link2telegramAPI();
  ```
  
## Documentation
### Send message
This method allows you to send messages using bots.
  ```java
  Link2telegramAPI.sendMsg("<Message>");
  ```
### GetUpdates
This method allows to get the last message received by the bot.
  ```java
  Link2telegramAPI.getUpdatedText()
  ```
Return value type: String
### TPS monitoring
This method can send a warning message when the server TPS exceeds or falls below a set threshold  
The set threshold can be modified in config.yml
#### Get TPS
   ````java
   Link2telegramAPI.getServerTPS();
   ````
Return value type: double[]
### Get basic server information
This method can return the current CPU and memory usage of the server
   ````java
   Link2telegramAPI.getServerStatus();
   ````
Return value type: int[CPU usage, memory usage]  
This method can also use the built-in Bot command `/status` to get the status
## Depends

[okhttp](https://github.com/square/okhttp)  
[java-telegram-bot-api](https://github.com/pengrad/java-telegram-bot-api)

## License
MIT