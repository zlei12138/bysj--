package bysj;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class View extends Frame{

	private static final int FRAMEWIDTH = 1500;
	private static final int FRAMEHEIGHT = 950;
	Image offScreen = null;

	private Button listenPortBtn;
	private Button sendDataBtn;
	private Button stopSocketBtn;
	private JTextField ip_text;
	private JTextField port_text;
	private JTextArea sended_Text;
	private ArrayList<JTextField> textFields;
	
	String roadNames[] = new String[] {
			"崇文路路段：","东水门大桥路段：","南滨路路段：","重庆长江大桥路段：","菜园坝大桥路段：",
			"真武山隧道路段：","腾龙大道路段：","通江大道路段：","丁香路路段：","北滨二路路段：",
			"北滨一路路段：","渝鲁大道路段：","海尔路路段：","沙滨路路段：","经纬大道路段：",
			"紫荆路路段：","金龙路路段：","巴县大道路段：","大江东路路段：","朝天门大桥路段："
	};

	

	public View() throws HeadlessException {
		super();
		textFields = new ArrayList();
		for (int i = 0; i < roadNames.length; i++) {
			JTextField jTextField = new JTextField();
			textFields.add(jTextField);
		}
	}

	public void launchFrame() {
		this.setBounds(200, 70, FRAMEWIDTH, FRAMEHEIGHT);	
		this.setTitle("路况发送客户端");
		this.setBackground(Color.WHITE);	
		this.setForeground(Color.MAGENTA);
		this.setLayout(null);
		this.addWindowListener(new WindowAdapter() {
			//添加对窗口状态的监听
			public void windowClosing(WindowEvent arg0) {
				//当窗口关闭时
				System.exit(0);	//退出程序
			}
			
		});

		this.setResizable(false);
		this.setVisible(true);	

		listenPortBtn = new Button("监听端口");
		listenPortBtn.setBounds(740, 280, 250, 50);
		listenPortBtn.setBackground(Color.lightGray);
		listenPortBtn.setFont(new Font("楷体", Font.CENTER_BASELINE, 20));
		add(listenPortBtn);
		
		stopSocketBtn = new Button("断开连接");
		stopSocketBtn.setBounds(1100, 280, 250, 50);
		stopSocketBtn.setBackground(Color.lightGray);
		stopSocketBtn.setFont(new Font("楷体", Font.CENTER_BASELINE, 20));
		add(stopSocketBtn);
		
		sendDataBtn = new Button("SEND DATA");
		sendDataBtn.setBounds(940, 800, 200, 50);
		sendDataBtn.setBackground(Color.lightGray);
		sendDataBtn.setFont(new Font("楷体", Font.CENTER_BASELINE, 20));
		add(sendDataBtn);
		
		for (int i = 0; i < roadNames.length; i++) {
			JTextField jTextField = textFields.get(i);
			jTextField.setText("0");
			jTextField.setBounds(370, 135+i*40, 120, 25);
			jTextField.setBackground(Color.white);
			jTextField.setFont(new Font("楷体", Font.ITALIC, 25));
			jTextField.setForeground(Color.gray);
			add(jTextField);
		}

		ip_text = new JTextField();
		ip_text.setText("");
		ip_text.setBounds(1070, 137, 160, 24);
		ip_text.setBackground(Color.white);
		ip_text.setFont(new Font("楷体", Font.ITALIC, 20));
		ip_text.setForeground(Color.gray);
		add(ip_text);
		
		port_text = new JTextField();
		port_text.setText("");
		port_text.setBounds(1070, 137+60, 160, 24);
		port_text.setBackground(Color.white);
		port_text.setFont(new Font("楷体", Font.ITALIC, 25));
		port_text.setForeground(Color.gray);
		add(port_text);
		
		sended_Text = new JTextArea();
		sended_Text.setLineWrap(true);        //激活自动换行功能 
		sended_Text.setWrapStyleWord(true);            // 激活断行不断字功能
		sended_Text.setText("");
		sended_Text.setBounds(620, 400, 850, 360);
		sended_Text.setBackground(Color.DARK_GRAY);
		sended_Text.setFont(new Font("楷体", Font.ITALIC, 25));
		sended_Text.setForeground(Color.gray);
		sended_Text.setEditable(false);
		add(sended_Text);
		
		new Thread(new RepaintThread()).start();	//开启重画线程
	}
	

	public Button getStopSocketBtn() {
		return stopSocketBtn;
	}

	public void setStopSocketBtn(Button stopSocketBtn) {
		this.stopSocketBtn = stopSocketBtn;
	}

	public Button getListenPortBtn() {
		return listenPortBtn;
	}

	public void setListenPortBtn(Button listenPortBtn) {
		this.listenPortBtn = listenPortBtn;
	}

	public Button getSendDataBtn() {
		return sendDataBtn;
	}

	public void setSendDataBtn(Button sendDataBtn) {
		this.sendDataBtn = sendDataBtn;
	}

	public JTextField getIp_text() {
		return ip_text;
	}

	public void setIp_text(JTextField ip_text) {
		this.ip_text = ip_text;
	}

	public JTextField getPort_text() {
		return port_text;
	}

	public void setPort_text(JTextField port_text) {
		this.port_text = port_text;
	}

	public JTextArea getSended_Text() {
		return sended_Text;
	}

	public void setSended_Text(JTextArea sended_Text) {
		this.sended_Text = sended_Text;
	}

	public ArrayList<JTextField> getTextFields() {
		return textFields;
	}

	public void setTextFields(ArrayList<JTextField> textFields) {
		this.textFields = textFields;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setFont(new Font("微软雅黑", Font.ITALIC, 30));
		g.setColor(Color.black);
		g.drawString("设置数据", 190, 90);
		
		g.setFont(new Font("楷体", Font.BOLD, 25));
		
		for (int i = 0; i < roadNames.length; i++) {
			g.drawString(roadNames[i], 40, 160+40*i);
		}
		
		g.drawString("设置IP：", FRAMEWIDTH/2+80, 160);
		g.drawString("设置PORT：", FRAMEWIDTH/2+80, 220);		
		g.drawLine(600, 0, 600, FRAMEHEIGHT);
		
		g.drawLine(600, 360, FRAMEWIDTH, 360);
		
		g.setFont(new Font("Tahoma", Font.BOLD, 12));
		g.drawString("Designed by ZL 2018 \\ www.zlei.top", FRAMEWIDTH-575, FRAMEHEIGHT-30);
		g.setFont(new Font("宋体", Font.BOLD, 12));
		g.drawString("毕业设计专用", FRAMEWIDTH-500, FRAMEHEIGHT-50);
	}
	
	
	
	@Override
	public void update(Graphics g) {
		if (offScreen == null)	offScreen = this.createImage(FRAMEWIDTH, FRAMEHEIGHT);
		Graphics gOffScreen = offScreen.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.white);
		gOffScreen.fillRect(0, 0, FRAMEWIDTH, FRAMEHEIGHT);	//重画背景画布
		this.paint(gOffScreen);	//重画界面元素
		gOffScreen.setColor(c);
		g.drawImage(offScreen, 0, 0, null);	//将新画好的画布“贴”在原画布上
	}
	private class RepaintThread implements Runnable {
		public void run() {
			while(true) {
				repaint();
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					System.exit(0);
				}
			}
		}
		
	}
}
