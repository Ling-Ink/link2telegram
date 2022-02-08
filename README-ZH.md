<h1 align="center">
    <img width="600" src="https://raw.githubusercontent.com/Crystal-Moling/link2telegram/master/Banner.png"/><br>
	Link2Telegram
</h1>

# 语言
[English](https://github.com/Crystal-Moling/link2telegram/blob/master/README.md) | 简体中文

# 介绍
通过插件调用Telegram Bot发送消息

## 使用方法
* 复制jar包到项目根目录
* 添加以下内容到pom.xml

        <dependency>
            <groupId>org.crystal.link2telegram</groupId>
            <artifactId>link2telegram</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/link2telegram-1.0-SNAPSHOT.jar</systemPath>
        </dependency>
* 添加以下内容到插件类
  ```java
  Link2telegramAPI Link2telegramAPI = new Link2telegramAPI();
  ```
* 发送信息
  ```java
  Link2telegramAPI.sendMsg("<待发送信息>");
  ```
## 依赖

[okhttp](https://github.com/square/okhttp)
[java-telegram-bot-api](https://github.com/pengrad/java-telegram-bot-api)

## License
MIT