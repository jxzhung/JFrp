import java.io.*;
import java.net.Socket;

/**
 * 内网端
 * 直接和服务器通信
 * Created by Jzhung on 2018/1/23.
 */
public class Frpc {
    public static void main(String[] args) {
        String server = "127.0.0.1";
        int port = 7778;
        new Frpc().startAndroidClient(server, port);
    }

    private void startAndroidClient(String server, int port) {
        try {
            Socket socket = new Socket(server, port);
            System.out.println("连接外网服务器成功");

            BufferedWriter nbw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader nbr = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            while ((line = nbr.readLine()) != null) {
                System.out.println("内网收到:" + line);
                String resp = "内网响应：" + line;
                nbw.write(resp);
                nbw.newLine();
                nbw.flush();
                System.out.println(resp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
