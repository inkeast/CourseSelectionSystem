import java.io.*;
import java.net.Socket;
import java.util.List;

public class TestClient {
    public static void main(String arav[])throws Exception {
        Socket socket = new Socket("127.0.0.1",2048);
        List list=null;

        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        while (true){
            dataOutputStream.writeUTF(bufferedReader.readLine()+"\n");
            dataOutputStream.flush();
            list = (List)objectInputStream.readObject();
            System.out.println(list);
        }

    }
}
