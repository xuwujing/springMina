package com.pcm.mina.client.SocketDemo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.pcm.mina.service.model.SentBody;


/**
 * @author ZERO
 * @Description java的简单的套接字连接，短连接，满足一次性的收发
 */
public class ScoketTest {
	public static void main(String[] args) {
		try {
			Socket socket = new Socket("127.0.0.1", 1255);
			PrintWriter os = new PrintWriter(socket.getOutputStream());
		    SentBody sy=new SentBody();
		    sy.put("message", "推送测试消息");
	        sy.put("account", "12345678"); //账号
	        sy.setKey("client_push");			
			os.println(sy);
			os.flush();
			BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String msg = is.readLine();
			System.out.println("客户端收到：" + msg);
			os.close();
			socket.close(); 

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("+++++客户端发生异常+++++");
		}
	}
}
