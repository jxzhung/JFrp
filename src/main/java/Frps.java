import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Java版内网穿透服务端
 * Created by Jzhung on 2018/1/23.
 */
public class Frps {
    private Socket transSocket; //内网长连接

    public static void main(String[] args) {
        Frps frps = new Frps();
        int sport = 7777;//用户客户端连接
        int tport = 7778;//用于内网连接
        frps.startServer(sport, tport);
    }

    private void startServer(final int sport, final int tport) {
        //启动控制端接收命令
        new Thread(new Runnable() {
            public void run() {
                startControlServer(sport);
            }
        }).start();

        //启动穿透长连接用于内网穿透
        startTransmissionServer(tport);
    }

    /**
     * 接受控制命令
     *
     * @param sport
     */
    private void startControlServer(int sport) {
        try {
            //监听控制端命令
            ServerSocket server = new ServerSocket(sport);
            System.out.println("启动接收WebSocket");
            while (true) {
                final Socket controlSocket = server.accept();
                new Thread(new Runnable() {
                    public void run() {
                        handleSocket(controlSocket);
                    }
                }).start();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSocket(Socket controlSocket) {
        System.out.println("收到WebSocket");
        if (transSocket == null) {
            System.out.println("没有可以下发的内网端");
            return;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(controlSocket.getOutputStream()));
            BufferedReader br = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));


            BufferedWriter nbw = new BufferedWriter(new OutputStreamWriter(transSocket.getOutputStream()));
            BufferedReader nbr = new BufferedReader(new InputStreamReader(transSocket.getInputStream()));

            String line;
            //读取客户端一行
            while ((line = br.readLine()) != null) {
                //写一行到内网
                System.out.println("服务器收到:" + line);
                nbw.write(line);
                nbw.newLine();
                nbw.flush();

                //读取内网返回一行
                line = nbr.readLine();

                //写一行到客户端
                System.out.println("转发回响应:" + line);
                bw.write(line);
                bw.newLine();
                bw.flush();
            }

            System.out.println("结束了");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 接受内网连接
     *
     * @param tport
     */
    private void startTransmissionServer(int tport) {
        try {
            //监听控制端命令
            ServerSocket server = new ServerSocket(tport);
            while (true) {
                Socket socket = server.accept();
                System.out.println("收到内网客户端连接");
                if (transSocket != null) {
                    System.out.println("关闭旧连接，接受新连接");
                    transSocket.close();
                }
                transSocket = socket;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
