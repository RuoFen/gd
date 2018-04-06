package day;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * 聊天室客户端
 * @author lu232
 *
 */
public class Client {
	 /**
	   * java.net.Socket 套接字
	   * 封装了TCP通讯协议。使用它可以基于TCP与远端计算机的
	   * 服务端应用程序链接并通讯
	   * 
	   */
		private Socket socket;

	public Client() throws Exception{
		try {
			/*
			 * 实例化Socket 就是与服务端建立连接的过程。
			 * 通过IP可以找到服务端计算机，再通过端口可以链接到运行在计算机上的
			 * 服务端程序。
			 */
			//初始化
			System.out.println("正在初始化...");
			//缓冲字符输入流按行读取
			BufferedReader br=new BufferedReader(
					new InputStreamReader(
							new FileInputStream(
									"config.txt"
									)
							)
					);
			String host=br.readLine();
			int port=Integer.parseInt(br.readLine());
			
			System.out.println("正在链接服务端...");
			socket =new Socket(host,port);
			System.out.println("与服务端建立连接！");
		} catch (Exception e) {
			/*
			 * 将来针对异常可能要记录日志，所以需要感知错误。
			 * 但是若异常不应当在这里被处理时可以继续在catch中
			 * 将其抛出
			 */
			throw e;
		}
	}
	/**
	 * 客户端开始工作的地方
	 */
	public void start(){
		try {
			Scanner scan=new Scanner(System.in);
			/*
			 * Socket提供方法：
			 * OutputStream getOutputStream()
			 * 通过Socket获取的输出流写出的字节
			 * 都会通过网络发送给远端计算机。
			 * 这里就等于发送给服务端了。
			 */
			//字节输出流
			OutputStream out=socket.getOutputStream();
			//转换流(字符输出流)
			OutputStreamWriter osw=
					new OutputStreamWriter(out,"UTF-8");
			//缓冲字符输出流
			PrintWriter pw=
					new PrintWriter(osw,true);
			
			//将读取服务端消息的线程启动
			ServerHandler handler=
					new ServerHandler();
			Thread t=new Thread(handler);
			t.start();
			
			String line=null;
			long lastSend=System.currentTimeMillis();
			while(true){
				line=scan.nextLine();
				if(System.currentTimeMillis()-lastSend>=1000){
				pw.println(line);
				lastSend=System.currentTimeMillis();
				}else{
					System.out.println("您说话太快，请休息一下...");
					lastSend=System.currentTimeMillis();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		
		try {
			//实例化客户端
			Client client = new Client();
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("客户端运行失败！");
		}
	}
	/**
	 * 该线程负责读取服务端发送过来的所有消息并
	 * 输出到控制台
	 * @author lu232
	 *
	 */
	private class ServerHandler implements Runnable{
		public void run(){
			try {
				//创建输入流
				InputStream in=socket.getInputStream();
				InputStreamReader isr=new InputStreamReader(in,"UTF-8");
				BufferedReader br=new BufferedReader(isr);
				
				String message=null;
				while((message=br.readLine())!=null){
					System.out.println(message);
				}
			} catch (Exception e) {
				
			}
		}
	}
		
}
