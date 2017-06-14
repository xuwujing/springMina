package com.pcm.mina.service.filter;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import com.pcm.mina.service.model.Message;

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
