import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;
import java.util.Base64.Encoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSocket {
  public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException {
    Integer port = 8080;
    ServerSocket server = new ServerSocket(port);
    try {
      

      System.out.println("Server has started on 127.0.0.1:" + port.toString() + ".\r\nWaiting for a connection…");
      System.out.println("atualizou");
      Socket client = server.accept();
      System.out.println("Client: " + client.toString());

      OutputStream out = client.getOutputStream();
      InputStream in = client.getInputStream();
      Scanner s = new Scanner(in, "UTF-8");
      
      try {
        String data = s.useDelimiter("\\r\\n\\r\\n").next();
        System.out.println("");
        System.out.println(data);
        System.out.println("");
        Matcher get = Pattern.compile("^GET").matcher(data);

        if (get.find()) {
          Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
          match.find();

          Encoder base64 = Base64.getEncoder();
          MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

          byte[] matchplusGUID = (match.group(1).trim() + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
              .getBytes("UTF-8");

          System.out.println("matchgroup:" + match.group(1));

          String acceptKey = base64.encodeToString(sha1
              .digest(matchplusGUID));

          System.out.println("AcceptKey: " + acceptKey);
          byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
              + "Connection: Upgrade\r\n"
              + "Upgrade: websocket\r\n"
              + "Sec-WebSocket-Accept: "
              + acceptKey + "\r\n"
              + "Sec-WebSocket-Protocol: chat"
              + "\r\n\r\n").getBytes("UTF-8");

          out.write(response, 0, response.length);
          out.flush();
          
        }

      } catch (IOException e) {
        
      } finally{
        s.close();

      }

    } catch (IOException e) {

    } finally {
      server.close();
    }
  }
}
