import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class SQLserver extends Thread {
    private static int port = 2048;
    public static Operating operating = new Operating();
    private static ServerSocket serverSocket;
    private static int MaxThread = 8 ;
    private static int ThreadNum = 0 ;
    private static Object lock = new Object();


    public void run(){
        String data;
        List result = null;
        try{
            Socket socket = serverSocket.accept();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            ObjectOutputStream  objectOutputStream = new ObjectOutputStream (socket.getOutputStream());
            while (((data = bufferedReader.readLine())!=null)){
                result = operating.dbms_online(data);
                objectOutputStream.writeUnshared(result);
                objectOutputStream.flush();
            }
            objectOutputStream.close();
            bufferedReader.close();
            socket.close();
            synchronized (lock){ThreadNum--;}
            this.interrupt();
        }
        catch (IOException io){
            synchronized (lock){ThreadNum--;}
            System.out.println(io);
            this.interrupt();
        }
    }

    public static void main(String [] args) throws Exception{
        serverSocket = new ServerSocket(port);
        operating.init();
        int now;
        while (true){
            synchronized (lock){now = ThreadNum;}
            if(now < MaxThread ){
                synchronized (lock){ThreadNum++;}
                SQLserver sockets = new SQLserver();
                sockets.start();
            }
            sleep(1);
        }
    }

}
