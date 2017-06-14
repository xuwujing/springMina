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
