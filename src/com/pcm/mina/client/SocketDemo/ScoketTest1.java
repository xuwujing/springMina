package com.pcm.mina.client.SocketDemo;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.pcm.mina.service.model.SentBody;

/**
 * @author ZERO
 * @Description  java的简单socket连接，长连接，尝试连续从服务器获取消息
 */
public class ScoketTest1 {
	public static final String IP_ADDR = "127.0.0.1";// 服务器地址
	public static final int PORT = 1255;// 服务器端口号
	static String text = null;
	public static void main(String[] args) throws IOException {
		System.out.println("客户端启动...");
		Socket socket = null;
		socket = new Socket(IP_ADDR, PORT);
		PrintWriter os = new PrintWriter(socket.getOutputStream());
		SentBody sy=new SentBody();
        sy.put("message", "这是个测试账号");
        sy.put("account", "12345678");
        sy.setKey("client_bind");
		os.println(sy);
		os.flush();
		while (true) {
			try {
				// 创建一个流套接字并将其连接到指定主机上的指定端口号
				DataInputStream input = new DataInputStream(socket.getInputStream());
				// 读取服务器端数据
				byte[] buffer;
				buffer = new byte[input.available()];
				if (buffer.length != 0) {
					System.out.println("length=" + buffer.length);
					// 读取缓冲区
					input.read(buffer);
					// 转换字符串
					String three = new String(buffer);
					System.out.println("内容=" + three);
					if (three.equals("hb_request")) {
						System.out.println("发送返回心跳包");
						os = new PrintWriter(socket.getOutputStream());
						os.println("hb_response");
						os.flush();
					}
				}
			} catch (Exception e) {
				System.out.println("客户端异常:" + e.getMessage());
				os.close();
			}
		}
	}
}
