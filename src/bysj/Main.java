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

		// �������� 
		View view = new View();
		view.launchFrame();

		// ��ȡip��ַ����ʾ��ip��λ����
		JTextField ip_text = view.getIp_text();
		setIPText(ip_text);

		// �����˿�
		Button listenPortBtn = view.getListenPortBtn();
		JTextField port_text = view.getPort_text();
		ListeningPort(listenPortBtn, port_text);

		// �Ͽ�����
		Button stopSocketBtn = view.getStopSocketBtn();
		disconnectSocket(stopSocketBtn, listenPortBtn);

		// ��ȡ����������
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
					System.err.println("�޷����ͣ�socket�ѶϿ�");
				} else {
					StringBuffer dataBuffer = new StringBuffer();
					for (int i = 0; i < textFields.size(); i++) {
						JTextField textField = textFields.get(i);
						String text = textField.getText();

						try {
							Integer.valueOf(text);
						} catch (Exception e) {
							System.err.println("������������δ����");
							e.printStackTrace();
							if (sended_Text.getLineCount() >= 6) {
								sended_Text.setText("");
							}
							sended_Text.append("������������δ����\n");
							return;
						}

						dataBuffer.append(i + "_" + text + "&");
					}
					dataBuffer.deleteCharAt(dataBuffer.length() - 1);
					String data = dataBuffer.toString();
					System.out.println("���͵����ݣ�" + data);

					try {
						OutputStream outputStream = socket.getOutputStream();
						outputStream.write(data.getBytes());
						if (sended_Text.getLineCount() >= 6) {
							sended_Text.setText("");
						}
						sended_Text.append(data + "\n");
					} catch (IOException e) {
						System.err.println("��ȡ�����ʧ��");
						if (sended_Text.getLineCount() >= 6) {
							sended_Text.setText("");
						}

						listenPortBtn.setLabel("���ӶϿ�������������");
						listenPortBtn.setForeground(Color.MAGENTA);
						sended_Text.append("����ʧ��\n");
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
						listenPortBtn.setLabel("�����˿�");
					} catch (IOException e) {
						System.err.println("�Ͽ�����ʧ��1");
						e.printStackTrace();
					}
				}

				if (socketServer != null) {
					try {
						socketServer.close();
						socketServer = null;
					} catch (IOException e) {
						System.err.println("�Ͽ�����ʧ��2");
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
							listenPortBtn.setLabel("���ڵȴ�����");
							socket = socketServer.accept();
							String hostAddress = socket.getInetAddress().getHostAddress();
							listenPortBtn.setLabel("�����ӣ�" + hostAddress);

						} catch (NumberFormatException e) {

							// �����쳣�Ժ�ر�socket
							listenPortBtn.setLabel("����ʧ�ܣ�����������");
							listenPortBtn.setForeground(Color.MAGENTA);

							try {
								socket.close();
								socketServer.close();
							} catch (IOException e1) {
								System.err.println("socket �ر�ʧ��");
								e1.printStackTrace();
							}
							socket = null;
							socketServer = null;

							System.err.println("Main��port����int");
							e.printStackTrace();
						} catch (IOException e) {

							// �����쳣�Ժ�ر�socket

							listenPortBtn.setLabel("����������");
							listenPortBtn.setForeground(Color.MAGENTA);
							try {
								socket.close();
								socketServer.close();
							} catch (IOException e1) {
								System.err.println("socket �ر�ʧ��");
								e1.printStackTrace();
							}
							socket = null;
							socketServer = null;

							System.err.println("Main������socketʧ��");
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
			// �������е�����ӿ�
			for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
				NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
				// �����еĽӿ����ٱ���IP
				for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
					if (!inetAddr.isLoopbackAddress()) {// �ų�loopback���͵�ַ
						if (inetAddr.isSiteLocalAddress()) {
							// �����site-local��ַ����������
							return inetAddr;
						} else if (candidateAddress == null) {
							// site-local���͵ĵ�ַδ�����֣��ȼ�¼��ѡ��ַ
							candidateAddress = inetAddr;
						}
					}
				}
			}
			if (candidateAddress != null) {
				return candidateAddress;
			}
			// ���û�з��� non-loopback��ַ.ֻ�������ѡ�ķ���
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			return jdkSuppliedAddress;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
