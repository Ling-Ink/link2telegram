<h1 align="center">
    <img src="https://raw.githubusercontent.com/Crystal-Moling/link2telegram/master/Banner.png"/><br>
	Link2Telegram
</h1>

# Language
English | [简体中文](https://raw.githubusercontent.com/Crystal-Moling/link2telegram/master/README-ZH.md)

# Description
Backup your Minecraft server to git

## Usage
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
* Send message
  ```java
  Link2telegramAPI.sendMsg("<Message>");
  ```
## Depends

[okhttp](https://github.com/square/okhttp)
[java-telegram-bot-api](https://github.com/pengrad/java-telegram-bot-api)

## License
MIT