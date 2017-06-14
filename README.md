# springMina
Spring整合Mina，实现消息推送以及消息转发。

原文博客地址:http://blog.csdn.net/qazwsxpcm/article/details/73255909
mina特点:
-------

基于java NIO类库开发；采用非阻塞方式的异步传输；事件驱动；支持批量数据传输；支持TCP、UDP协议；控制反转的设计模式（支持Spring）；采用优雅的松耦合架构；可灵活的加载过滤器机制；单元测试更容易实现；可自定义线程的数量，以提高运行于多处理器上的性能；采用回调的方式完成调用，线程的使用更容易。 

spring集成mina:
-------------

在学习mina这块时，在网上找了很多资料，但是感觉少了一些什么，不够完善。于是在项目中成功运用mina之后，又重新总结归纳了mina这块。于是便开始通过spring整合mina，实现消息推送以及转发。可以实现客户端A向服务端发送消息，服务端将消息转发给客户端B。

效果实现图:
服务端启动成功后， 客户端A绑定服务端。

![这里写图片描述](http://img.blog.csdn.net/20170614220230745?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWF6d3N4cGNt/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

客户端B向服务端发送信息，请求服务端向客户端A推送消息
![这里写图片描述](http://img.blog.csdn.net/20170614220337043?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWF6d3N4cGNt/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

客户端A受到服务端转发的客户端B的消息
![这里写图片描述](http://img.blog.csdn.net/20170614220412669?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWF6d3N4cGNt/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

服务端心跳检测的实现
![这里写图片描述](http://img.blog.csdn.net/20170614220520497?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWF6d3N4cGNt/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


那么开始实现代码的编写。(可以直接跳到底部，通过链接下载工程代码)
首先在官网上下载mina以及spring相关架包，这里相关架包已准备好:http://download.csdn.net/detail/qazwsxpcm/9870787


服务端：
====

1. 首先实现数据传输对象、消息常量的代码编写。
------------------------

 我使用的两个传输对象，接受和发送，代码如下。(传输对象可以自行定义)。

```
package com.pcm.mina.service.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author ZERO
 * @Description 服务端接收消息对象
 */
public class SentBody implements Serializable {

	private static final long serialVersionUID = 1L;

	private String key;

	private HashMap<String, String> data;

	private long timestamp;

	public SentBody() {
		data = new HashMap<String, String>();
		timestamp = System.currentTimeMillis();
	}

	public String getKey() {
		return key;
	}

	public String get(String k) {
		return data.get(k);
	}

	public void put(String k, String v) {
		data.put(k, v);
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void remove(String k) {
		data.remove(k);
	}

	public HashMap<String, String> getData() {
		return data;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("<sent>");
		buffer.append("<key>").append(key).append("</key>");
		buffer.append("<timestamp>").append(timestamp).append("</timestamp>");
		buffer.append("<data>");
		for (String key : data.keySet()) {
			buffer.append("<" + key + ">").append(data.get(key)).append(
					"</" + key + ">");
		}
		buffer.append("</data>");
		buffer.append("</sent>");
		return buffer.toString();
	}

	public String toXmlString() {
		return toString();
	}
}
```


```
package com.pcm.mina.service.model;

import java.io.Serializable;
import java.util.HashMap;
/**
 * @author ZERO
 * @Description 服务端发送消息对象
 */
public class ReplyBody implements Serializable {
 
	private static final long serialVersionUID = 1L;

	/**
	 * 请求key
	 */
	private String key;
	
	/**
	 * 返回码
	 */
	private String code;
	
	/**
	 * 返回说明
	 */
	private String message;

	/**
	 * 返回数据集合
	 */
	private HashMap<String, String> data;

	
	private long timestamp;
	
	public ReplyBody()
	{
		data = new HashMap<String, String>();
		timestamp = System.currentTimeMillis();
	}
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	 

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void put(String k, String v) {
		data.put(k, v);
	}

	public String get(String k) {
		return data.get(k);
	}

	public void remove(String k) {
		data.remove(k);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public HashMap<String, String> getData() {
		return data;
	}
  
	public void setData(HashMap<String, String> data) {
		this.data = data;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	
	public String toString()
	{
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("<reply>");
		buffer.append("<key>").append(this.getKey()).append("</key>");
		buffer.append("<timestamp>").append(timestamp).append("</timestamp>");
		buffer.append("<code>").append(code).append("</code>");
		buffer.append("<message>").append(message).append("</message>");
		buffer.append("<data>");
		for(String key:this.getData().keySet())
		{
			buffer.append("<"+key+">").append(this.get(key)).append("</"+key+">");
		}
		buffer.append("</data>");
		buffer.append("</reply>");
		return buffer.toString();
	}

	
	public String toXmlString()
	{
		
		return toString();
	}
}

```

```
package com.pcm.mina.service.model;

/**
 * @author ZERO
 * @Description 消息常量
 */
public class Message {
	public static class ReturnCode {

		public static String CODE_404 = "404"; 

		public static String CODE_403 = "403";  //该账号未绑定

		public static String CODE_405 = "405"; //事物未定义
        
		public static String CODE_200 = "200"; //成功

		public static String CODE_500 = "500"; //未知错误

	}


	public static final String SESSION_KEY = "account";


	/**
	 * 服务端心跳请求命令
	 */
	public static final String CMD_HEARTBEAT_REQUEST = "hb_request";
	/**
	 * 客户端心跳响应命令
	 */
	public static final String CMD_HEARTBEAT_RESPONSE = "hb_response";


	public static class MessageType {
		// 用户会 踢出下线消息类型
		public static String TYPE_999 = "999";

	}

}

```


2，实现心跳检测功能。
-----------

服务端发送的是hb_request，那么客户端就应该返回hb_response，以此来实现心跳检测。

```
/**
 * @author ZERO
 * @Description  心跳协议的实现类
 */ 
public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory{
	private final Logger LOG=Logger.getLogger(KeepAliveMessageFactoryImpl.class);
	/**
	 * 客户端心跳响应命令
	 */
	private static  String HEARRESPONSE=Message.CMD_HEARTBEAT_RESPONSE; 
	/**
	 * 服务端心跳请求命令
	 */
	private static  String HEARREQUEST=Message.CMD_HEARTBEAT_REQUEST;
	
	public Object getRequest(IoSession session) {
		LOG.warn("请求预设信息:"+HEARREQUEST);
		return HEARREQUEST;
	}

	public Object getResponse(IoSession session, Object message) {
		 LOG.warn("响应预设信息: " + message);  
	        /** 返回预设语句 */  
	      return HEARRESPONSE;  
	}

	public boolean isRequest(IoSession session, Object message) {
		LOG.warn("请求心跳包信息: " + message);  
        return message.equals(HEARREQUEST); 
	}

	public boolean isResponse(IoSession session, Object message) {
		 LOG.warn("响应心跳包信息: " + message);  
	     return message.equals(HEARRESPONSE);
	   }
}
```

3， 实现服务端代码编写
------------
服务端代码这块，因为注释写的已经够详细了，所以这里就不细说了。
```
package com.pcm.mina.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import com.pcm.mina.service.filter.KeepAliveMessageFactoryImpl;


/**
 * @author ZERO
 * @Description  mina服务端
 */
public class SerNioSociketAcceptor {
	IoAcceptor acceptor;
    IoHandler ioHandler;
    int port;
	//记录日志
	public static Logger logger=Logger.getLogger(SerNioSociketAcceptor.class);
   //创建bind()方法接收连接
    public void bind() throws IOException
    {	
       //创建 协议编码解码过滤器ProtocolCodecFilter
       //设置序列化Object  可以自行设置自定义解码器
     	ProtocolCodecFilter pf=new ProtocolCodecFilter(new ObjectSerializationCodecFactory());
       //getFilterChain() 获取 I/O 过滤器链，可以对 I/O 过滤器进行管理，包括添加和删除 I/O 过滤器。    	
    	acceptor = new NioSocketAcceptor();  
    	//设置缓存大小
        acceptor.getSessionConfig().setReadBufferSize(1024);  
         // 设置过滤器
        acceptor.getFilterChain().addLast("executor",new ExecutorFilter()); 
        acceptor.getFilterChain().addLast("logger",new LoggingFilter());  
        acceptor.getFilterChain().addLast("codec",pf);
        
        KeepAliveMessageFactory kamf=new KeepAliveMessageFactoryImpl();
        KeepAliveFilter kaf = new KeepAliveFilter(kamf, IdleStatus.BOTH_IDLE);
        kaf.setForwardEvent(true);
        kaf.setRequestInterval(30);  //本服务器为被定型心跳  即需要每30秒接受一个心跳请求  否则该连接进入空闲状态 并且发出idled方法回调
        acceptor.getFilterChain().addLast("heart", kaf); 
        //读写通道60秒内无操作进入空闲状态
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);
        //绑定逻辑处理器
        acceptor.setHandler(ioHandler);  
        //绑定端口
        acceptor.bind(new InetSocketAddress(port));
        logger.info("Mina服务端启动成功...端口号为:"+port); //测试使用
    }
    //创建unbind()方法停止监听
    public void unbind()
    {
    	acceptor.unbind();
    	logger.info("服务端停止成功");
    }
	public void setAcceptor(IoAcceptor acceptor) {
		this.acceptor = acceptor;
	}
	//	设置 I/O 处理器。该 I/O 处理器会负责处理该 I/O 服务所管理的所有 I/O 会话产生的 I/O 事件。
	public void setIoHandler(IoHandler ioHandler) {
		this.ioHandler = ioHandler;
	}
	//设置端口
	public void setPort(int port) {
		this.port = port;
	}
//	获取该 I/O 服务所管理的 I/O 会话。
	public  Map<Long, IoSession> getManagedSessions()
	{
		return acceptor.getManagedSessions();
	}
}

```

4，实现session容器
-------------
如果需要保证线程安全，可以使用 ConcurrentHashMap，作为session容器。
```
package com.pcm.mina.service.session;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import org.apache.mina.core.session.IoSession;


/**
 * @author ZERO
 * @Description  IoSession包装类  
 */
public class PcmSession implements Serializable{

	private static final long serialVersionUID = 1L;
   
	private transient IoSession session;
	
	private String gid;				//session全局ID
	private Long nid;				//session在本台服务器上的ID
	private String host;			//session绑定的服务器IP
	private String account;			//session绑定的账号
	private String message;			//session绑定账号的消息
	private Long bindTime;			//登录时间
	private Long heartbeat;			//心跳时间


	public PcmSession(){}
	
	public PcmSession(IoSession session) {
		this.session = session;
		this.nid = session.getId();
	}
   
	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public Long getBindTime() {
		return bindTime;
	}

	public void setBindTime(Long bindTime) {
		this.bindTime = bindTime;
	}

	public Long getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(Long heartbeat) {
		this.heartbeat = heartbeat;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setIoSession(IoSession session) {
		this.session = session;
	}

	public IoSession getIoSession() {
		return session;
	}
	

//  将键为 key，值为 value的用户自定义的属性存储到 I/O 会话中。
	public void setAttribute(String key, Object value) {
		if(session!=null){
			session.setAttribute(key, value);
		}
		
	}


	public boolean containsAttribute(String key) {
		if(session!=null){
			
			return session.containsAttribute(key);
		}
		return false;
	}
	//	从 I/O 会话中获取键为 key的用户自定义的属性。
	public Object getAttribute(String key) {
		if(session!=null){
			
			return session.getAttribute(key);
		}
		
		return null;
	}
	//从 I/O 会话中删除键为 key的用户自定义的属性。
	public void removeAttribute(String key) {
		if(session!=null){
			session.removeAttribute(key);
		}
	}
		

	public SocketAddress getRemoteAddress() {
		if(session!=null){
			return session.getRemoteAddress();
		}		
		return null;
	}

/*	 将消息对象 message发送到当前连接的对等体。该方法是异步的，当消息被真正发送到对等体的时候，
	IoHandler.messageSent(IoSession,Object)会被调用。如果需要的话，
	也可以等消息真正发送出去之后再继续执行后续操作。*/
	public void write(Object msg) {
		if(session!=null)
		{
			session.write(msg).isWritten();	
		}
	}
	
	public boolean isConnected() {
		if(session!=null){
			return session.isConnected();
		}
		return false;
	}

	public boolean  isLocalhost()
	{
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			return ip.equals(host);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return false;
		 
	}
	
/* 关闭当前连接。如果参数 immediately为 true的话，
 * 连接会等到队列中所有的数据发送请求都完成之后才关闭；否则的话就立即关闭。 
 */	
	public void close(boolean immediately) {
		if(session!=null){
			session.close(immediately);
		}
	}

	public boolean equals(Object message) {
        
		if (message instanceof PcmSession) {
			
			PcmSession t = (PcmSession) message;
			if( t.nid!=null && nid!=null)
			{
				return  t.nid.longValue()==nid.longValue() && t.host.equals(host);
			} 
		}  
		return false;
	}

	
	public String  toString()
	{
		StringBuffer buffer = new   StringBuffer();
		buffer.append("{");
	buffer.append("\"").append("gid").append("\":").append("\"").append(gid).append("\"").append(",");
		buffer.append("\"").append("nid").append("\":").append(nid).append(",");
		buffer.append("\"").append("host").append("\":").append("\"").append(host).append("\"").append(",");
		buffer.append("\"").append("account").append("\":").append("\"").append(account).append("\"").append(",");
		buffer.append("\"").append("bindTime").append("\":").append(bindTime).append(",");
		buffer.append("\"").append("heartbeat").append("\":").append(heartbeat);
		buffer.append("}");
		return buffer.toString();
		
	}
}

```
```
package com.pcm.mina.service.session;

/**
 * @author ZERO
 * @Description  客户端的session管理接口
 */
public interface SessionManager {
	/**
	 * 添加新的session
	 */
	public void addSession(String account,PcmSession session);
	
	/**
	 * 
	 * @param account 客户端session的 key 一般可用 用户账号来对应session
	 * @return
	 */
	PcmSession getSession(String account);
	
    /**
	 * 删除session
	 * @param session
	 */
    public void  removeSession(PcmSession session);
  
    /**
  	 * 删除session
  	 * @param account
  	 */
    public void  removeSession(String account);
    
}

```

```
package com.pcm.mina.service.session;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.pcm.mina.service.model.Message;

/**
 * @author ZERO
 * @Description  自带默认 session管理实现
 */
public class DefaultSessionManager implements SessionManager{

	 private static HashMap<String,PcmSession> sessions =new  HashMap<String,PcmSession>();
	 private static final AtomicInteger connectionsCounter = new AtomicInteger(0);

	public void addSession(String account, PcmSession session) {
		if(session !=null){
			sessions.put(account, session);
			connectionsCounter.incrementAndGet();
		}
	}
	
	public PcmSession getSession(String account) {
		return sessions.get(account);
	}

	public void removeSession(PcmSession session) {
		sessions.remove(session.getAttribute(Message.SESSION_KEY));
	}
	
	public void removeSession(String account) {
		sessions.remove(account);
	}
	
}

```


 
 

5， 实现业务逻辑处理器。
--------------------------
因为注释写的已经够详细了，所以这里就不细说了。
做了简单业务逻辑处理，如有需要可以自行更改。

```
package com.pcm.mina.service.handler;

import java.net.InetSocketAddress;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Component;
import com.pcm.mina.service.RequestHandler;
import com.pcm.mina.service.model.Message;
import com.pcm.mina.service.model.ReplyBody;
import com.pcm.mina.service.model.SentBody;
import com.pcm.mina.service.session.PcmSession;

/**
 * @author ZERO
 * @Description  I/O 处理器 客户端请求的入口，所有请求都首先经过它分发处理 业务逻辑实现
 */ 

@Component("sercixeMainHandler")
public class ServiceMainHandler extends IoHandlerAdapter{
	protected final Logger logger = Logger.getLogger(ServiceMainHandler.class);
	
    //本地handler请求
	private HashMap<String, RequestHandler> handlers = new HashMap<String, RequestHandler>();
			
	//出错时
	@Override
	public void exceptionCaught(IoSession session, Throwable cause){
		logger.error("exceptionCaught()... from "+session.getRemoteAddress());
		logger.error(cause);
		cause.printStackTrace();
	}
	
	//接收到消息时
	@Override
	public void messageReceived(IoSession iosession,Object message){		
		logger.info("服务端接收到的消息..."+message.toString());
		if(message instanceof SentBody){
			SentBody sent=(SentBody) message;
			ReplyBody rb=new ReplyBody();
			PcmSession session=new PcmSession(iosession);
			String key=sent.getKey();
			if("quit".equals(sent.get("message"))){ //服务器断开的条件
				try {
					sessionClosed(iosession);
				} catch (Exception e) {
					rb.setCode(Message.ReturnCode.CODE_500);
					e.printStackTrace();
				}
			}else{
			//根据key的不同调用不同的handler
			RequestHandler rhandler=handlers.get(key);
			if(rhandler==null){//如果没有这个handler
				rb.setCode(Message.ReturnCode.CODE_405);
				rb.setMessage("服务端未定义!");
			}else{//有的话
				rb=rhandler.process(session, sent);
			}
			}
			if(rb !=null){
				rb.setKey(key);
				session.write(rb);
				logger.info("服务端发送的消息: " + rb.toString());
			}
		
		}
	}
	
	//发送消息
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
   //	session.close(); //发送成功后主动断开与客户端的连接 实现短连接
       logger.info("服务端发送信息成功...");
	    }
	
	//建立连接时
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		 	InetSocketAddress sa=(InetSocketAddress)session.getRemoteAddress();
			String address=sa.getAddress().getHostAddress(); //访问的ip
			session.setAttribute("address", address);
			//将连接的客户端ip保存到map集合中
			SentBody body=new SentBody();
			body.put("address", address);
			logger.info("访问的ip:"+address);
	    }
	
	//关闭连接时   
	@Override
	public void sessionClosed(IoSession iosession) throws Exception {
			PcmSession session=new PcmSession(iosession);
			logger.debug("sessionClosed()... from "+session.getRemoteAddress());
			try {
				RequestHandler hand=handlers.get("client_closs");
				if(hand !=null && session.containsAttribute(Message.SESSION_KEY)){
					hand.process(session, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			session.close(true);  
			logger.info("连接关闭");
	    }

	//空闲时
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		logger.debug("sessionIdle()... from "+session.getRemoteAddress());
	    }
	
	//打开连接时   
	@Override
	 public void sessionOpened(IoSession session) throws Exception {
		 	logger.info("开启连接...");
	    }
	
	public HashMap<String, RequestHandler> getHandlers() {
		return handlers;
	}
	public void setHandlers(HashMap<String, RequestHandler> handlers) {
		this.handlers = handlers;
	} 
} 

```


6， 实现业务逻辑代码。
------------

目前实现了绑定，推送以及关闭逻辑代码。如有需要，可自行增加。

```
package com.pcm.mina.service;

import com.pcm.mina.service.model.ReplyBody;
import com.pcm.mina.service.model.SentBody;
import com.pcm.mina.service.session.PcmSession;
/**
 * @author ZERO
 * @Description  请求处理接口,所有的请求必须实现此接口
 */
public interface RequestHandler {
	public abstract ReplyBody process(PcmSession session,SentBody message);
}

```


```
package com.pcm.mina.service.handler;

import java.net.InetAddress;
import java.util.UUID;
import org.apache.log4j.Logger;
import com.pcm.mina.service.RequestHandler;
import com.pcm.mina.service.model.Message;
import com.pcm.mina.service.model.ReplyBody;
import com.pcm.mina.service.model.SentBody;
import com.pcm.mina.service.session.DefaultSessionManager;
import com.pcm.mina.service.session.PcmSession;
import com.pcm.util.ContextHolder;
 
/**
 * @author ZERO
 * @Description  账号绑定实现
 */ 
public class BindHandler implements RequestHandler {
	protected final Logger logger = Logger.getLogger(BindHandler.class);
	public ReplyBody process(PcmSession newSession, SentBody message) {
		ReplyBody reply = new ReplyBody();
		DefaultSessionManager sessionManager= ((DefaultSessionManager) ContextHolder.getBean("PcmSessionManager"));
		try { 
			String account = message.get(Message.SESSION_KEY);
			newSession.setAccount(account);
			newSession.setMessage(message.get("message"));
			newSession.setGid(UUID.randomUUID().toString());
			newSession.setHost(InetAddress.getLocalHost().getHostAddress());
            //第一次设置心跳时间为登录时间
			newSession.setBindTime(System.currentTimeMillis());
			newSession.setHeartbeat(System.currentTimeMillis());
			/**
			 * 由于客户端断线服务端可能会无法获知的情况，客户端重连时，需要关闭旧的连接
			 */
			PcmSession oldSession  = sessionManager.getSession(account);
            //如果是账号已经在另一台终端登录。则让另一个终端下线
			if(oldSession!=null&&!oldSession.equals(newSession))
			{
					oldSession.removeAttribute(Message.SESSION_KEY);
					ReplyBody rb = new ReplyBody();
					rb.setCode(Message.MessageType.TYPE_999);//强行下线消息类型
					rb.put(Message.SESSION_KEY, account);
					if(!oldSession.isLocalhost())
					{
						/*
						判断当前session是否连接于本台服务器，如不是发往目标服务器处理
						MessageDispatcher.execute(rb, oldSession.getHost());
						*/
					}else
					{
						oldSession.write(rb);
						oldSession.close(true);
						oldSession = null;
					}
					oldSession = null;
			}
			if(oldSession==null)
			{
				sessionManager.addSession(account, newSession);
			}
			
			reply.setCode(Message.ReturnCode.CODE_200);
		} catch (Exception e) {
			reply.setCode(Message.ReturnCode.CODE_500);
			e.printStackTrace();
		}
		logger.debug("绑定账号:" +message.get(Message.SESSION_KEY)+"-----------------------------" +reply.getCode());
		return reply;
	}
	
}
```

```
package com.pcm.mina.service.handler;

import org.apache.log4j.Logger;
import com.pcm.mina.service.RequestHandler;
import com.pcm.mina.service.model.Message;
import com.pcm.mina.service.model.ReplyBody;
import com.pcm.mina.service.model.SentBody;
import com.pcm.mina.service.session.DefaultSessionManager;
import com.pcm.mina.service.session.PcmSession;
import com.pcm.util.ContextHolder;

/**
 * @author ZERO
 * @Description  推送消息
 */ 
public class PushMessageHandler implements RequestHandler {

	protected final Logger logger = Logger.getLogger(PushMessageHandler.class);
	public ReplyBody process(PcmSession ios, SentBody sent) {
		ReplyBody reply = new ReplyBody();
		String account=(String) sent.getData().get(Message.SESSION_KEY);
		DefaultSessionManager sessionManager=(DefaultSessionManager) ContextHolder.getBean("PcmSessionManager");
		PcmSession session=sessionManager.getSession(account);
		if(session !=null){
			sent.remove(Message.SESSION_KEY);
			reply.setKey(sent.getKey());
			reply.setMessage("推送的消息");
			reply.setData(sent.getData());
			reply.setCode(Message.ReturnCode.CODE_200); 
			session.write(reply); //转发获取的消息
			logger.info("推送的消息是:"+reply.toString());
		}else{
			reply.setCode(Message.ReturnCode.CODE_403);
			reply.setMessage("推送失败");
		}
		return reply;
	}
}
```

 

```
package com.pcm.mina.service.handler;

import org.apache.log4j.Logger;
import com.pcm.mina.service.RequestHandler;
import com.pcm.mina.service.model.Message;
import com.pcm.mina.service.model.ReplyBody;
import com.pcm.mina.service.model.SentBody;
import com.pcm.mina.service.session.DefaultSessionManager;
import com.pcm.mina.service.session.PcmSession;
import com.pcm.util.ContextHolder;

/**
 * @author ZERO
 * @Description  断开连接，清除session
 */
public class SessionClosedHandler implements RequestHandler {
	protected final Logger logger = Logger.getLogger(SessionClosedHandler.class);
	public ReplyBody process(PcmSession ios, SentBody message) {

		DefaultSessionManager sessionManager  =  ((DefaultSessionManager) ContextHolder.getBean("PcmSessionManager"));

		if(ios.getAttribute(Message.SESSION_KEY)==null)
		{
			return null;
		}
	    String account = ios.getAttribute(Message.SESSION_KEY).toString();
	    sessionManager.removeSession(account);
		return null;
	}
}
```


7，spring配置
----------
可以将过滤器添加到spring这块，包括心跳设置。
```
<!-- spring集成mina -->
		<!-- 设置 I/O 接受器，并指定接收到请求后交给 myHandler 进行处理 --> 
		<bean id="customEditorConfigurer" class="org.springframework.beans.factory.config.CustomEditorConfigurer">
		   <property name="customEditors" >
		     <map>
		       <entry key="java.net.SocketAddress"  value="org.apache.mina.integration.beans.InetSocketAddressEditor"/>
		     </map>
		   </property>
		 </bean>
		 
		 <!-- handlers事件 -->
		<bean id="IoHandler" class="com.pcm.mina.service.handler.ServiceMainHandler">
			<property name="handlers">
				<map>
					<entry key="client_bind">  <!-- 创建连接 -->
						<bean class="com.pcm.mina.service.handler.BindHandler"></bean>
					</entry>
					<entry key="client_closs">  <!--断开清除会话  -->
						<bean class="com.pcm.mina.service.handler.SessionClosedHandler"></bean>
					</entry>
					<entry key="client_push">  <!--在线推送消息  -->
						<bean class="com.pcm.mina.service.handler.PushMessageHandler"></bean>
					</entry>
				</map>
			</property>
		</bean>
		
		 <!-- IoAccepter，绑定到1255端口 -->
		  <!-- 通过 init-method指明了当 I/O 接受器创建成功之后，调用其 bind方法来接受连接；通过 destroy-method声明了当其被销毁的时候，调用其 unbind来停止监听 -->
	    <bean id="SerNioSociketAcceptor"  class="com.pcm.mina.service.SerNioSociketAcceptor" 
	    init-method="bind" destroy-method="unbind">  
	         <property name="port" value="1255" /> 
			 <property name="ioHandler" ref="IoHandler" /> 
	    </bean>
	     
	     <!--spring动态获取bean实现  -->
	    <bean id="ContextHolder" class="com.pcm.util.ContextHolder"></bean>
	   	<bean id="PcmSessionManager" class="com.pcm.mina.service.session.DefaultSessionManager"/> 
```
	   

客户端
===

1，编写业务逻辑处理器
-----------
几乎和服务端一样，这里因为测试，所以就从简了。

```
package com.pcm.mina.client.MinaDemo;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * @author ZERO
 * @Description 客户端handle
 */
public class MinaClientHandler extends IoHandlerAdapter {
    private static Logger logger = Logger.getLogger(MinaClientHandler.class);

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
         String msg = message.toString();
    //    logger.info("客户端A接收的数据:" + msg);
      System.out.println("客户端A接收的数据:" + msg);
       if(msg.equals("hb_request")){
        logger.warn("客户端A成功收到心跳包:hb_request");
       	session.write("hb_response");
       	logger.warn("客户端A成功发送心跳包:hb_response");
       }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        logger.error("发生错误...", cause);
    }
}
```

2，编写客户端程序。
----------
也几乎和服务端一致，为了简单使用，编写main方法。
注:客户端和服务端的过滤器要一致。

```
package com.pcm.mina.client.MinaDemo;

import java.net.InetSocketAddress;
import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import com.pcm.mina.service.model.SentBody;


/**
 * @author ZERO
 * @Description mina 客户端
 */
	public class MinaClient {
	    private static Logger logger = Logger.getLogger(MinaClient.class);
	    private static String HOST = "127.0.0.1";
	    private static int PORT = 1255;
	    private static  IoConnector connector=new NioSocketConnector();
	    private static   IoSession session;
	    public static IoConnector getConnector() {
			return connector;
		}

		public static void setConnector(IoConnector connector) {
			MinaClient.connector = connector;
		}

		/* 
	    * 测试服务端与客户端程序！
	    a. 启动服务端，然后再启动客户端
	    b. 服务端接收消息并处理成功;
	    */
	    @SuppressWarnings("deprecation")
		public static void main(String[] args) {
	    	   // 设置链接超时时间
	        connector.setConnectTimeout(30000);
	        // 添加过滤器  可序列话的对象 
	        connector.getFilterChain().addLast(
	                "codec",
	                new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
	        // 添加业务逻辑处理器类
	        connector.setHandler(new MinaClientHandler());
	        ConnectFuture future = connector.connect(new InetSocketAddress(
	                HOST, PORT));// 创建连接
	        future.awaitUninterruptibly();// 等待连接创建完成
	        session = future.getSession();// 获得session
	        
	     	bindstart();
	   // 	pushstart();
	    }
	    
	    public static void bindstart(){
	    	logger.info("客户端A绑定服务端");
	        try {
	            SentBody sy=new SentBody();
	            sy.put("message", "这是个测试账号");
	            sy.put("account", "123456");
	            sy.setKey("client_bind");
	            session.write(sy);// 发送消息
	            System.out.println("客户端A与服务端建立连接成功...发送的消息为:"+sy);
	      //      logger.info("客户端A与服务端建立连接成功...发送的消息为:"+sy);
	        } catch (Exception e) {
	        	e.printStackTrace();
	            logger.error("客户A端链接异常...", e);
	        }
	        session.getCloseFuture().awaitUninterruptibly();// 等待连接断开
	        connector.dispose();
	    }
｝	    
```

代码就先告一段落。客户端也可以通过socket和mina进行数据传输，这里就不贴代码了。
spring整合mina，暂时就到这了。项目我放到了github上，地址:https://github.com/xuwujing/springMina/tree/master
如果感觉不错，希望可以给个star。


