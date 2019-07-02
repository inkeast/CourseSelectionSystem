import java.io.*;
import java.net.Socket;
import java.util.List;

public class TestClient {
    public static void main(String arav[])throws Exception {
        Socket socket = new Socket("127.0.0.1",2048);
        List list=null;

        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        dataOutputStream.writeUTF("select * from student where SID = 10 ;\n");
        list = (List)objectInputStream.readObject();
        System.out.println(list);
    }
}
