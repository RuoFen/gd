package day;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天室服务端
 * @author lu232
 *
 */
public class Server {
	/*
	 * 运行在服务端的java.net.ServerSocket
	 * 主要有两个作用：
	 * 1.向系统申请对外的窗口，客户端Socket就是
	 * 通过这个端口与服务端程序建立链接的。
	 * 2.监听该服务端，一旦一个客户端Socket通过该端口尝试建立链接，ServerSocket就会感知并实例化
	 * Socket与该客户端进行通讯
	 */
	private ServerSocket server;
	/*
	 * 存放所有客户端输出流，用于广播消息给
	 * 所有客户端
	 */
	private List<PrintWriter> allOut;

	/**
	 * 构造方法用来初始化服务端
	 * @throws Exception 
	 */
	public Server() throws Exception {
		try {
			System.out.println("正在启动服务端...");
			server=new ServerSocket(8088);
			allOut=new ArrayList<PrintWriter>();
			System.out.println("服务端启动完毕！");
			
		} catch (Exception e) {
			throw e;
		}
	}
	/*
	 * 服务端开始工作的地方
	 */
	public void start(){
		try {
			/*
			 * ServerSocket提供方法：
			 * Socket accept()
			 * 该方法是一个阻塞方法，用于监听
			 * 服务端口，直到一个客户端连接上
			 * 为止，这里会返回一个Socket,
			 * 通过这个Socket就可以与该客户端进行通讯了。			 * 
			 */
		while(true){	
			System.out.println("等待客户端链接...");
			Socket socket=server.accept();	
			System.out.println("一个客户端连接了！");
			
			//一个客户端连接后启动一个线程处理客户端消息
			ClientHandler handler=
					new ClientHandler(socket);
			Thread t=new Thread(handler);
			t.start();
		}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			Server server=new Server();
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("服务端运行失败！");
		}
	}
	
	/**
	 * 该线程负责通过给定的Socket与指定客户端进行通讯
	 * @author lu232
	 *
	 */
	private class ClientHandler implements Runnable{
		
		private Socket socket;
		//客户端的地址信息
		private String host;
		
		public ClientHandler(Socket socket){
			this.socket=socket;
			//通过Socket获取远端计算机地址信息
			InetAddress address=
					socket.getInetAddress();
			//获取客户端IP地址的字符串格式内容
			host=address.getHostAddress();
		}
		
		public void run(){
			PrintWriter pw=null;
			try {
				/*
				 * 通过Socket获取输入流，读取的数据就是来自
				 * 远端计算机发送过来的数据。这里相当于读取的是
				 * 客户端发送过来的数据
				 */
				InputStream in=socket.getInputStream();
				InputStreamReader isr=new InputStreamReader(in,"UTF-8");
				BufferedReader br=
						new BufferedReader(isr);
				
				//通过Socket获取输出流用于将消息发送给客户端
				OutputStream out=socket.getOutputStream();
				OutputStreamWriter osw=new OutputStreamWriter(out,"UTF-8");
				pw=new PrintWriter(osw,true);
				
				//将该客户端的输出流放入共享集合(这里写(this)没用，它指代ClientHandler)
				synchronized (allOut) {
					allOut.add(pw);
				}
				
				String message=null;
				while((message=br.readLine())!=null){
					
//					message=br.readLine();
//					System.out.println("客户端说："+message);
//					pw.println("我自己说："+message);
					
					synchronized (allOut) {
						//遍历共享集合，将消息发给所有客户端
						for(PrintWriter o:allOut){
							o.println(host+"说："+message);
						}
					}
					
				}
			} catch (Exception e) {
				
			}finally{
				//处理客户端断开连接后的操作
				synchronized (allOut) {
					//1：先将客户端的输出流从共享集合里删除
					allOut.remove(pw);
				}
				
				//将对应客户端的Socket关闭以释放资源
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
