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