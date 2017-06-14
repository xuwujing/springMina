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
