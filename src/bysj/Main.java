package bysj;

import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Main {

	static ServerSocket socketServer = null;
	static Socket socket = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// 启动界面 
		View view = new View();
		view.launchFrame();

		// 获取ip地址并显示在ip的位置上
		JTextField ip_text = view.getIp_text();
		setIPText(ip_text);

		// 监听端口
		Button listenPortBtn = view.getListenPortBtn();
		JTextField port_text = view.getPort_text();
		ListeningPort(listenPortBtn, port_text);

		// 断开连接
		Button stopSocketBtn = view.getStopSocketBtn();
		disconnectSocket(stopSocketBtn, listenPortBtn);

		// 获取并发送数据
		Button sendDataBtn = view.getSendDataBtn();
		ArrayList<JTextField> textFields = view.getTextFields();
		JTextArea sended_Text = view.getSended_Text();
		SendData(sendDataBtn, textFields, sended_Text, listenPortBtn);
	}

	private static void setIPText(JTextField ip_text) {
		try {
			InetAddress inetAddress = getLocalHostLANAddress();
			String hostAddress = inetAddress.getHostAddress();
			ip_text.setText(hostAddress);
		} catch (Exception e) {
			ip_text.setText("169.254.245.131");
			e.printStackTrace();
		}
	}

	private static void SendData(Button sendDataBtn, ArrayList<JTextField> textFields, JTextArea sended_Text,
			Button listenPortBtn) {

		sendDataBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (socket == null) {
					System.err.println("无法发送，socket已断开");
				} else {
					StringBuffer dataBuffer = new StringBuffer();
					for (int i = 0; i < textFields.size(); i++) {
						JTextField textField = textFields.get(i);
						String text = textField.getText();

						try {
							Integer.valueOf(text);
						} catch (Exception e) {
							System.err.println("数据输入有误，未发送");
							e.printStackTrace();
							if (sended_Text.getLineCount() >= 6) {
								sended_Text.setText("");
							}
							sended_Text.append("数据输入有误，未发送\n");
							return;
						}

						dataBuffer.append(i + "_" + text + "&");
					}
					dataBuffer.deleteCharAt(dataBuffer.length() - 1);
					String data = dataBuffer.toString();
					System.out.println("发送的数据：" + data);

					try {
						OutputStream outputStream = socket.getOutputStream();
						outputStream.write(data.getBytes());
						if (sended_Text.getLineCount() >= 6) {
							sended_Text.setText("");
						}
						sended_Text.append(data + "\n");
					} catch (IOException e) {
						System.err.println("获取输出流失败");
						if (sended_Text.getLineCount() >= 6) {
							sended_Text.setText("");
						}

						listenPortBtn.setLabel("连接断开，请重新连接");
						listenPortBtn.setForeground(Color.MAGENTA);
						sended_Text.append("发送失败\n");
						e.printStackTrace();
					}
				}
			}
		});
	}

	private static void disconnectSocket(Button stopSocketBtn, Button listenPortBtn) {
		stopSocketBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (socket != null) {
					try {
						socket.close();
						socket = null;
						listenPortBtn.setForeground(Color.MAGENTA);
						listenPortBtn.setLabel("监听端口");
					} catch (IOException e) {
						System.err.println("断开连接失败1");
						e.printStackTrace();
					}
				}

				if (socketServer != null) {
					try {
						socketServer.close();
						socketServer = null;
					} catch (IOException e) {
						System.err.println("断开连接失败2");
						e.printStackTrace();
					}
				}
			}
		});
	}

	private static void ListeningPort(Button listenPortBtn, JTextField port_text) {

		listenPortBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				String port = port_text.getText();

				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							socketServer = new ServerSocket(Integer.valueOf(port));
							listenPortBtn.setForeground(Color.GRAY);
							listenPortBtn.setLabel("正在等待连接");
							socket = socketServer.accept();
							String hostAddress = socket.getInetAddress().getHostAddress();
							listenPortBtn.setLabel("已连接：" + hostAddress);

						} catch (NumberFormatException e) {

							// 出现异常以后关闭socket
							listenPortBtn.setLabel("连接失败，请重新连接");
							listenPortBtn.setForeground(Color.MAGENTA);

							try {
								socket.close();
								socketServer.close();
							} catch (IOException e1) {
								System.err.println("socket 关闭失败");
								e1.printStackTrace();
							}
							socket = null;
							socketServer = null;

							System.err.println("Main：port不是int");
							e.printStackTrace();
						} catch (IOException e) {

							// 出现异常以后关闭socket

							listenPortBtn.setLabel("请重新连接");
							listenPortBtn.setForeground(Color.MAGENTA);
							try {
								socket.close();
								socketServer.close();
							} catch (IOException e1) {
								System.err.println("socket 关闭失败");
								e1.printStackTrace();
							}
							socket = null;
							socketServer = null;

							System.err.println("Main：创建socket失败");
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
	}

	private static InetAddress getLocalHostLANAddress() throws Exception {
		try {
			InetAddress candidateAddress = null;
			// 遍历所有的网络接口
			for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
				NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
				// 在所有的接口下再遍历IP
				for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
					if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
						if (inetAddr.isSiteLocalAddress()) {
							// 如果是site-local地址，就是它了
							return inetAddr;
						} else if (candidateAddress == null) {
							// site-local类型的地址未被发现，先记录候选地址
							candidateAddress = inetAddr;
						}
					}
				}
			}
			if (candidateAddress != null) {
				return candidateAddress;
			}
			// 如果没有发现 non-loopback地址.只能用最次选的方案
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			return jdkSuppliedAddress;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
