import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class SQLserver extends Thread {
    private static int port = 2048;
    public static Operating operating = new Operating();
    private static ServerSocket serverSocket;
    private static int MaxThread = 1024 ;
    private static int ThreadNum = 0 ;
    private Socket socket;
    private static Object lock = new Object();

    SQLserver(Socket socket){this.socket = socket;}

    public static void  readsocket()
    {
        try{File file=new File(".\\socket.txt");
            InputStreamReader read = new InputStreamReader(
                    new  FileInputStream(file),"utf-8");
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while((lineTxt = bufferedReader.readLine()) != null){

                if(lineTxt.equals("databaseport:")){port=Integer.parseInt(bufferedReader.readLine());}

            }
            read.close();}catch (Exception e){};
    }

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
                if(result.size()!=0&&result.get(0).equals("exit")){
                    System.exit(0);
                }
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
        Socket socket ;
        readsocket();
        serverSocket = new ServerSocket(port);
        operating.init();
        while (true){
            if(ThreadNum < MaxThread ){
                synchronized (lock){ThreadNum++;}
                socket = serverSocket.accept();
                SQLserver sockets = new SQLserver(socket);
                sockets.start();
            }
            //sleep(1);
        }
    }
}
