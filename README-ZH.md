<h1 align="center">
    <img width="500" src="https://raw.githubusercontent.com/Crystal-Moling/link2telegram/master/Banner.png"/><br>
	Link2Telegram
</h1>

# 语言
[English](https://github.com/Crystal-Moling/link2telegram/blob/master/README.md) | 简体中文

# 介绍
通过插件连接到Telegram Bot

## 加载
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
## 文档
### 发送消息
该方法允许你使用机器人发送消息
  ```java
  Link2telegramAPI.sendMsg("<待发送的消息>");
  ```
### 获取消息
该方法允许获取机器人收到的最近一条消息
  ```java
  Link2telegramAPI.getUpdatedText()
  ```
返回值类型：String
### TPS监测
该方法可以在服务器TPS超出或低于设定的阈值时发送警告消息  
设定的阈值可以在config.yml中修改
#### 获取TPS
  ```java
  Link2telegramAPI.getServerTPS();
  ```
返回值类型：double[]
### 获取服务器基本信息
该方法可以返回服务器当前的CPU和内存使用情况
  ```java
  Link2telegramAPI.getServerStatus();
  ```
返回值类型：int[CPU占用,内存占用]  
该方法也可以用自带Bot命令`/status`获取状态
## 依赖

[okhttp](https://github.com/square/okhttp)  
[java-telegram-bot-api](https://github.com/pengrad/java-telegram-bot-api)

## License
MIT