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
