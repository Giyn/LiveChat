# LiveChat

## :spiral_notepad: Introduction

:two_men_holding_hands: 本项目基于 Java 的 Swing 包实现了一个带图形用户界面的基于数据加密算法的即时聊天系统。实现了用户登录及验证、在线聊天、好友管理、文件传输、聊天记录管理和数据传输加密等功能。

编程语言：Java；

JDK 版本：1.8.0；

开发工具：IntelliJ IDEA（版本：2020.2.1）；

数据库：MySQL（版本：8.0.19 for Win64 on x86_64）；

服务器：阿里云服务器（版本：Windows Server 2019 数据中心版 64 位中文版）

程序架构：C/S 架构；

## :point_right: Instruction

先运行 `LiveChat/Server.java` 文件，然后运行 `LiveChat/Client.java` 文件（支持多开）即可。

## :black_nib: DemandAnalysis

**需求分析图：**

![78e8f4071226ef856f471d3c24fda25.png](https://i.loli.net/2021/04/18/tcFnhWbZxlijRk6.png)

1. 用户登录：

   用户在登录界面输入用户 ID 和密码点击登录，后台系统接收到数据后进行用户身份验证，若验证通过则进入系统主界面；若没有通过用户身份验证则显示错误。

2. 登录验证：

   用户点击登录按钮后，后台系统接收到用户输入的信息后，在数据库的用户信息表进行查询，若验证成功则成功进入系统主界面；若验证失败则返回对应的错误信息。

3. 聊天功能：

   在好友列表中选择一名好友后，打开与其聊天界面，若该好友在线，则可以向其发起即时聊天，对方收到消息后也会弹出聊天窗口。

4. 文件传输：

   在与好友聊天时，可以进行文件/图片的发送，对方接收到文件后可以保存在聊天记录文件夹中。

5. 好友管理：

   用户可以在好友列表中进行添加或删除好友，添加好友时会向对方发送申请，对方收到通知并通过申请后方能成为好友，好友列表的信息会存储在数据库中，在好友关系解除后该数据也会清除。

6. 在线、离线状态显示：

   后台服务器动态地请求用户的所有好友的在线状态，并实时返回信息，以实现显示所有好友当前是否在线，达到较好的鲁棒性。

7. 聊天记录管理：

   后台服务器将用户与好友的聊天记录以文件的形式存储于本地，用户无需配置数据库即可在本地导入聊天记录，以便聊天记录的查询。

8. 数据传输加密：

   计算机网络安全最为重要的内容是为用户提供安全可靠的保密通信，其中最常用的手段是密码机制。在该系统中所采用的加密算法是 RSA 加密算法和 MD5 加密算法。其中用户的密码使用 MD5 加密后存储于数据库，用户与用户间的聊天记录信息使用 RSA 加密算法后形成密文在后台服务器进行传输，好友经过共享密钥进行解密后才能得到明文，基于以上的加密操作来防止中间攻击导致的数据泄露，

**系统通信架构图：**

![fab66585298b7dfb4e097ce6223e302.png](https://i.loli.net/2021/04/18/yX5R49FBp8TtOrL.png)

**软件功能架构图：**

![787b84e2e7bd4f73aff4168bb9853f6.png](https://i.loli.net/2021/04/18/hFIEMrJGacHPpLg.png)

## :heart_decoration: Features

1. **用户登录**

<img src="https://i.loli.net/2021/04/22/Lm4fFxzw65BqWdD.png" alt="970630b8fbcc5b613e9b646959b9338.png" style="zoom:80%;" />

2. **好友列表**

<img src="https://i.loli.net/2021/04/22/dfVlPwtXQUKmgry.png" alt="19ba3eed401dfc9cee04388c90c842d.png" style="zoom: 80%;" />

3. **添加好友**

![29cfb4ff2dc861eb0d15235f8d5e6d6.png](https://i.loli.net/2021/04/22/ec6LQRZbnuU1Fv7.png)

![eac529f12823de7aa536fbd9b3e9508.png](https://i.loli.net/2021/04/22/39ykeEdBrwtLKm8.png)

4. **好友申请**

<img src="https://i.loli.net/2021/04/22/WFKe8PgpD3OIXnw.png" alt="c75ddf82588d7bc086c2a0359b87cfb.png" style="zoom:67%;" />

<img src="https://i.loli.net/2021/04/22/7LcaWM6NoqmSZtY.png" alt="98d57789d97379138e653b703dd38f3.png" style="zoom:80%;" />

5. **聊天功能**

<img src="https://i.loli.net/2021/04/22/vAX5r94nzae2Bfq.png" alt="ecda00ff487bc920d1eb5b78bb96f62.png" style="zoom: 67%;" />

6. **发送图片/文件**

<img src="https://i.loli.net/2021/04/22/PpcTgBZ4CGkv1Jx.png" alt="7637c47cabe231d6d3ea3fcd4f18529.png" style="zoom:80%;" />

<img src="https://i.loli.net/2021/04/22/3SgntIvr5xZGhJU.png" alt="1ee8c5f6189db34bced813f01478e6e.png" style="zoom:80%;" />