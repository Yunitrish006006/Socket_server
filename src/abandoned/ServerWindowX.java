package abandoned;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.util.Enumeration;

class ServerWindowX extends JFrame {
    static int portNumber = 25565;

    static Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    public ServerWindowX() {

        super.setTitle("Server Socket");
        super.setLayout(null);
        super.setResizable(false);
        super.setBounds(0, 0, screen.width / 4, screen.height / 4 + 20);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextArea contentField = new JTextArea();
        contentField.setName("content");
        contentField.setText("");
        contentField.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        contentField.setBounds(10, 10, screen.width / 4 - 40, screen.height / 4 - 40);
        super.add(contentField);
        super.setVisible(true);

        byte[] buffer = new byte[10];
        String message;
        int portNumber = 25565;
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                DatagramSocket socket = new DatagramSocket(portNumber);
                socket.receive(packet);
                message = new String(buffer, 0, packet.getLength());
                System.out.println("Socket Receive: " + message);
                socket.close();
            } catch (Exception ignored) {
            }
        }
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
