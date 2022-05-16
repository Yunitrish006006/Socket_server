package abandoned;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

class ClientWindowX extends JFrame {
    static String serverIP = "";
    static String content = "this is the test content";
    static int portNumber = 25565;

    static Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    public ClientWindowX() {

        super.setTitle("Client Socket");
        super.setLayout(null);
        super.setResizable(false);
        super.setBounds(0, 0, screen.width / 4, screen.height / 4);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField sourceIPField = new JTextField();
        sourceIPField.setName("serverIP");
        try {
            sourceIPField.setText("Source IP: " + getLocalHostLANAddress().toString().substring(1));
        } catch (UnknownHostException e) {
            sourceIPField.setText("");
            throw new RuntimeException(e);
        }
        sourceIPField.setColumns(20);
        sourceIPField.setEditable(false);
        sourceIPField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                serverIP = sourceIPField.getText();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        sourceIPField.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        sourceIPField.setBounds(30, 0, 400, 30);
        super.add(sourceIPField);

        JTextField destinationIPField = new JTextField();
        destinationIPField.setName("serverIP");
        try {
            destinationIPField.replaceSelection(getLocalHostLANAddress().toString().substring(1));
        } catch (UnknownHostException e) {
            destinationIPField.setText("");
            throw new RuntimeException(e);
        }
        destinationIPField.setColumns(20);
        destinationIPField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                serverIP = destinationIPField.getText();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        destinationIPField.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        destinationIPField.setBounds(30, 50, 400, 30);
        super.add(destinationIPField);

        JTextField contentField = new JTextField();
        contentField.setName("content");
        contentField.setText(content);
        contentField.setColumns(20);
        contentField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                content = contentField.getText();
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        contentField.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        contentField.setBounds(30, 100, 400, 30);
        super.add(contentField);

        JButton sendButton = new JButton();
        sendButton.setName("sendButton");
        sendButton.setText("send");
        sendButton.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        sendButton.addActionListener(e -> {
            InetAddress address = null;
            try {
                address = InetAddress.getByName(serverIP);
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
            try {
                address = InetAddress.getByName(serverIP);
                if (content.getBytes().length <= 10) {
                    DatagramPacket packet = new DatagramPacket(content.getBytes(), content.getBytes().length, address, portNumber);
                    DatagramSocket socket = new DatagramSocket();
                    socket.send(packet);
                    socket.close();
                } else {
                    int count = content.length() / 10;
                    int i = 0;

                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    while (i < count) {
                        System.out.println("[" + formatter.format(date) + "] " + "(m1)[" + content.substring(i * 10, (i + 1) * 10).getBytes().length + "]\t" + content.substring(i * 10, (i + 1) * 10));
                        DatagramPacket packet = new DatagramPacket(content.substring(i * 10, (i + 1) * 10).getBytes(), content.substring(i * 10, (i + 1) * 10).getBytes().length, address, portNumber);
                        DatagramSocket socket = new DatagramSocket();
                        socket.send(packet);
                        socket.close();
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        i++;
                    }
                    System.out.println("[" + formatter.format(date) + "] " + "(m2)[" + content.substring(count * 10).getBytes().length + "]\t" + content.substring(count * 10));
                    DatagramPacket packet = new DatagramPacket(content.substring(count * 10).getBytes(), content.substring(count * 10).getBytes().length, address, portNumber);
                    DatagramSocket socket = new DatagramSocket();
                    socket.send(packet);
                    socket.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        sendButton.setBounds(30, 150, 400, 30);
        super.add(sendButton);

        super.setVisible(true);
    }

    private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
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
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException(
                    "Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }
}
