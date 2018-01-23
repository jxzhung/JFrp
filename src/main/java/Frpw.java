import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * 客户端
 * 微信WebSocket
 * Created by Jzhung on 2018/1/23.
 */
public class Frpw {
    public static void main(String[] args) {
        String server = "127.0.0.1";
        int port = 7777;
        new Frpw().startWebSocket(server, port);
    }

    private void startWebSocket(String server, int port) {
        try {
            Socket socket = new Socket(server, port);
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String data = "{\\\"name\\\":\\\"BeJson\\\",\\\"url\\\":\\\"http://www.bejson.com\\\",\\\"page\\\":88,\\\"isNonProfit\\\":true,\\\"address\\\":{\\\"street\\\":\\\"科技园路.\\\",\\\"city\\\":\\\"江苏苏州\\\",\\\"country\\\":\\\"中国\\\"}}";

            while (true) {
                bw.write(data);
                bw.newLine();//行分割
                //bw.newLine();//命令结束
                bw.flush();

                System.out.println("客户端发送：" + data);

                String line;
                while ((line = br.readLine()) != null) {
                    /*if (line.equals("")) {
                        System.out.println("跳过接收数据");
                        break;
                    }*/
                    System.out.println("客户端收到：" + line);
                    break;
                }

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
