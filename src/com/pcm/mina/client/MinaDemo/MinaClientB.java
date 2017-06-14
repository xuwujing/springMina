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
	public class MinaClientB {
	    private static Logger logger = Logger.getLogger(MinaClientB.class);
	    private static String HOST = "127.0.0.1";
	    private static int PORT = 1255;
	    private static  IoConnector connector=new NioSocketConnector();
	    private static   IoSession session;
	    public static IoConnector getConnector() {
			return connector;
		}

		public static void setConnector(IoConnector connector) {
			MinaClientB.connector = connector;
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
	        connector.setHandler(new MinaClientHandlerB());
	        ConnectFuture future = connector.connect(new InetSocketAddress(
	                HOST, PORT));// 创建连接
	        future.awaitUninterruptibly();// 等待连接创建完成
	        session = future.getSession();// 获得session
	    	pushstart();
	    }
	    
	   
	    public static void pushstart(){
	    	logger.info("客户端B请求服务端推送");
	       try {
	           SentBody sy=new SentBody();
	           sy.put("message", "推送测试消息");
	           sy.put("account", "123456"); //账号
	           sy.setKey("client_push");
	           session.write(sy);// 发送消息
	           System.out.println("客户端B与服务端建立连接成功...发送的消息为:"+sy);
//	           logger.info("客户端B与服务端建立连接成功...发送的消息为:"+sy);
	       } catch (Exception e) {
	       	e.printStackTrace();
	           logger.error("客户B端链接异常...", e);
	       }
	       session.getCloseFuture().awaitUninterruptibly();// 等待连接断开
	       connector.dispose();
	   }
}
