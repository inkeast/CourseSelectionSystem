import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer{
    public static void main(String[] args)throws Exception{
        ServerSocket server = new ServerSocket(9999);
        Socket socket = server.accept();
        System.out.println("Accept!");
        String line;

        BufferedReader bs = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        PrintWriter os = new PrintWriter(socket.getOutputStream());

        BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("client:" + bs.readLine());

        line = sin.readLine();

        while (!line.equals("bye")){
            os.println(line);
            os.flush();
            System.out.println("server:" + line);
            System.out.println("client:" + bs.readLine());
            line = sin.readLine();
        }
        os.close();
        bs.close();
        socket.close();
        server.close();

    }

}