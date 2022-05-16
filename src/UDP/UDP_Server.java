package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDP_Server {
    public static void main(String[] args)throws IOException {
        String str_send = "Hello UDP client";
        byte[] buf = new byte[1024];
        //服務端在3000埠監聽接收到的資料
        DatagramSocket ds = new DatagramSocket(3000);
        //接收從客戶端傳送過來的資料
        DatagramPacket dp_receive = new DatagramPacket(buf, 1024);
        System.out.println("server is on，waiting for client to send data......");
        boolean f = true;
        while(f){
            //伺服器端接收來自客戶端的資料
            ds.receive(dp_receive);
            System.out.println("server received data from client：");
            String str_receive = new String(dp_receive.getData(),0,dp_receive.getLength()) +
                    " from " + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();
            System.out.println(str_receive);
            //資料發動到客戶端的3000埠
            DatagramPacket dp_send= new DatagramPacket(str_send.getBytes(),str_send.length(),dp_receive.getAddress(),9000);
            ds.send(dp_send);
            //由於dp_receive在接收了資料之後，其內部訊息長度值會變為實際接收的訊息的位元組數，
            //所以這裡要將dp_receive的內部訊息長度重新置為1024
            dp_receive.setLength(1024);
        }
        ds.close();
    }
}
