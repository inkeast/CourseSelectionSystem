import java.io.*;
import java.net.Socket;
import java.util.List;

public class TestClient {
    public static void main(String arav[])throws Exception {
        Socket socket = new Socket("127.0.0.1",2048);
        List list=null;

        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        dataOutputStream.writeUTF("insert into student(SID,classes,sname,sex) values(4,1802,夙玉,女) ;\n");
        list = (List)objectInputStream.readObject();
        System.out.println(list);
    }
}
