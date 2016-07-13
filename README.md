# websocketQuartz
Under springFramework, using Quartz Scheduler to implement  distributed webSocket client re-connection . 

github源码下载：https://github.com/171388204/websocketQuartz


由于个人水平有限,如有错误,请指正!

如果这个有帮助到您,请多多支持!
Star me ! Fork me !
https://github.com/171388204/websocketQuartz

这个是基于github开源代码Java-WebSocket,将webSocket客户端集成到springframework框架中.

特点:

1 集成spring,使用xml配置文件加载,webSocket配置文件使用properties.

2 内存模式-客户端自动重连, 单点部署,单线连接webSocket,远程服务器TCP关闭自动重连.

3 quartz模式-客户端本身不自动重连,依靠quartz定时器进行自动重连.

4 quartz模式下,配置quartz为JobStoreTX,可以借助quartz分布式锁特性,实现客户端分布式部署.


使用说明:

1 webSocket配置文件:prop/webSocket.properties
webSocket.url=wss://127.0.0.1:8888

2 默认是启用quartz JobStoreTX.
关闭quartz定时任务,
a 在resources/spring/applicationContext.xml注释掉quartz配置文件
b 在resources/spring/webSocketConf.xml修改/或删除WebSocketFactoryBean的属性quartzMode=false(默认是false)

3 在test中有webSocketServer 服务代码,启动后可以用来进行本地测试.


联系方式:171388204@qq.com
