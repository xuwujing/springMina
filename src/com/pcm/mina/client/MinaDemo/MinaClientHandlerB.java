package com.pcm.mina.client.MinaDemo;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * @author ZERO
 * @Description 客户端handle
 */
public class MinaClientHandlerB extends IoHandlerAdapter {
    private static Logger logger = Logger.getLogger(MinaClientHandlerB.class);

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
         String msg = message.toString();
//        logger.info("客户端B接收的数据:" + msg);
         System.out.println("客户端B接收的数据:" + msg);
       if(msg.equals("hb_request")){
        logger.warn("客户端B成功收到心跳包:hb_request");
       	session.write("hb_response");
       	logger.warn("客户端B成功发送心跳包:hb_response");
       }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        logger.error("发生错误...", cause);
    }
}
