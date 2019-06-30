import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class socket {
     private static int port = 2048;

     public static void main(String [] args) throws Exception{
         ServerSocket serverSocket = new ServerSocket(port);
         Socket socket = serverSocket.accept();
         Operating operating = new Operating();
         operating.init();

         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
         ObjectOutputStream  objectOutputStream = new ObjectOutputStream (socket.getOutputStream());
         String x;
         List result = null;
         while ((x = bufferedReader.readLine())!=null){
             System.out.println(x);
             result = operating.dbms_online(x);
             objectOutputStream.writeUnshared(result);
             objectOutputStream.flush();
         }

     }

}
