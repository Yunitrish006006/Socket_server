package UDP;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDP_Client {
    private static final int TIMEOUT = 5000;  //設定接收資料的超時時間
    private static final int MAXNUM = 5;      //設定重發資料的最多次數
    public static void main(String[] args)throws IOException {
        String str_send = "Hello UDP server";
        byte[] buf = new byte[1024];
        //客戶端在9000埠監聽接收到的資料
        DatagramSocket ds = new DatagramSocket(9000);
        InetAddress loc = InetAddress.getLocalHost();
        //定義用來發送資料的DatagramPacket例項
        DatagramPacket dp_send= new DatagramPacket(str_send.getBytes(),str_send.length(),loc,3000);
        //定義用來接收資料的DatagramPacket例項
        DatagramPacket dp_receive = new DatagramPacket(buf, 1024);
        //資料發向本地3000埠
        ds.setSoTimeout(TIMEOUT);              //設定接收資料時阻塞的最長時間
        int tries = 0;                         //重發資料的次數
        boolean receivedResponse = false;     //是否接收到資料的標誌位
        //直到接收到資料，或者重發次數達到預定值，則退出迴圈
        while(!receivedResponse && tries<MAXNUM){
            //傳送資料
            ds.send(dp_send);
            try{
                //接收從服務端傳送回來的資料
                ds.receive(dp_receive);
                //如果接收到的資料不是來自目標地址，則丟擲異常
                if(!dp_receive.getAddress().equals(loc)){
                    throw new IOException("Received packet from an unknown source");
                }
                //如果接收到資料。則將receivedResponse標誌位改為true，從而退出迴圈
                receivedResponse = true;
            }catch(InterruptedIOException e){
                //如果接收資料時阻塞超時，重發並減少一次重發的次數
                tries += 1;
                System.out.println("Time out," + (MAXNUM - tries) + " more tries..." );
            }
        }
        if(receivedResponse){
            //如果收到資料，則打印出來
            System.out.println("client received data from server：");
            String str_receive = new String(dp_receive.getData(),0,dp_receive.getLength()) +
                    " from " + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();
            System.out.println(str_receive);
            //由於dp_receive在接收了資料之後，其內部訊息長度值會變為實際接收的訊息的位元組數，
            //所以這裡要將dp_receive的內部訊息長度重新置為1024
            dp_receive.setLength(1024);
        }else{
            //如果重發MAXNUM次資料後，仍未獲得伺服器傳送回來的資料，則列印如下資訊
            System.out.println("No response -- give up.");
        }
        ds.close();
    }
}