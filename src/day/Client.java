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
 * �����ҿͻ���
 * @author lu232
 *
 */
public class Client {
	 /**
	   * java.net.Socket �׽���
	   * ��װ��TCPͨѶЭ�顣ʹ�������Ի���TCP��Զ�˼������
	   * �����Ӧ�ó������Ӳ�ͨѶ
	   * 
	   */
		private Socket socket;

	public Client() throws Exception{
		try {
			/*
			 * ʵ����Socket ���������˽������ӵĹ��̡�
			 * ͨ��IP�����ҵ�����˼��������ͨ���˿ڿ������ӵ������ڼ�����ϵ�
			 * ����˳���
			 */
			//��ʼ��
			System.out.println("���ڳ�ʼ��...");
			//�����ַ����������ж�ȡ
			BufferedReader br=new BufferedReader(
					new InputStreamReader(
							new FileInputStream(
									"config.txt"
									)
							)
					);
			String host=br.readLine();
			int port=Integer.parseInt(br.readLine());
			
			System.out.println("�������ӷ����...");
			socket =new Socket(host,port);
			System.out.println("�����˽������ӣ�");
		} catch (Exception e) {
			/*
			 * ��������쳣����Ҫ��¼��־��������Ҫ��֪����
			 * �������쳣��Ӧ�������ﱻ����ʱ���Լ�����catch��
			 * �����׳�
			 */
			throw e;
		}
	}
	/**
	 * �ͻ��˿�ʼ�����ĵط�
	 */
	public void start(){
		try {
			Scanner scan=new Scanner(System.in);
			/*
			 * Socket�ṩ������
			 * OutputStream getOutputStream()
			 * ͨ��Socket��ȡ�������д�����ֽ�
			 * ����ͨ�����緢�͸�Զ�˼������
			 * ����͵��ڷ��͸�������ˡ�
			 */
			//�ֽ������
			OutputStream out=socket.getOutputStream();
			//ת����(�ַ������)
			OutputStreamWriter osw=
					new OutputStreamWriter(out,"UTF-8");
			//�����ַ������
			PrintWriter pw=
					new PrintWriter(osw,true);
			
			//����ȡ�������Ϣ���߳�����
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
					System.out.println("��˵��̫�죬����Ϣһ��...");
					lastSend=System.currentTimeMillis();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		
		try {
			//ʵ�����ͻ���
			Client client = new Client();
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("�ͻ�������ʧ�ܣ�");
		}
	}
	/**
	 * ���̸߳����ȡ����˷��͹�����������Ϣ��
	 * ���������̨
	 * @author lu232
	 *
	 */
	private class ServerHandler implements Runnable{
		public void run(){
			try {
				//����������
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
