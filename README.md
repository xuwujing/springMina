# springMina
Spring整合Mina，实现消息推送以及消息转发。

在eclipse中导入该项目，并将项目放到tomcat中，成功运行项目之后，运行项目中的MinaClient类。即可查看信息。

原文博客地址:http://blog.csdn.net/qazwsxpcm/article/details/73255909

mina特点:
-------

基于java NIO类库开发；采用非阻塞方式的异步传输；事件驱动；支持批量数据传输；支持TCP、UDP协议；控制反转的设计模式（支持Spring）；采用优雅的松耦合架构；可灵活的加载过滤器机制；单元测试更容易实现；可自定义线程的数量，以提高运行于多处理器上的性能；采用回调的方式完成调用，线程的使用更容易。 

spring集成mina:
-------------

在学习mina这块时，在网上找了很多资料，只有一些demo，只能实现客户端向服务端发送消息、建立长连接之类。但是实际上在项目中，并不简单实现这些，还有业务逻辑之类的处理以及消息的推送之类的。于是就单独建立了一个工程项目，能够实现客户端和服务端相互之间发送消息、建立长连接、实现心跳检测等功能。 

效果实现图:
服务端启动成功后， 客户端A绑定服务端。

![这里写图片描述](http://img.blog.csdn.net/20170614220230745?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWF6d3N4cGNt/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

客户端B向服务端发送信息，请求服务端向客户端A推送消息
![这里写图片描述](http://img.blog.csdn.net/20170614220337043?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWF6d3N4cGNt/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

客户端A受到服务端转发的客户端B的消息
![这里写图片描述](http://img.blog.csdn.net/20170614220412669?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWF6d3N4cGNt/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

服务端心跳检测的实现
![这里写图片描述](http://img.blog.csdn.net/20170614220520497?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWF6d3N4cGNt/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)





