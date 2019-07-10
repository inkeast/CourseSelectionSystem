import java.io.*;
import java.net.*;
import java.util.List;

public class ClientSocket {
    private BufferedReader dis = null;
    private PrintWriter dos = null;
    List list=null;
    ObjectInputStream objectInputStream=null;

    public ClientSocket(String host, int port) throws Exception {
        Socket s = new Socket(host, port); // 这个Socket对象创建完毕后何时销毁？
        dis = new BufferedReader(new InputStreamReader(s.getInputStream()));
        dos = new PrintWriter(s.getOutputStream());
        objectInputStream = new ObjectInputStream(s.getInputStream());
    }

    public void userLookCourse()throws Exception{
        dos.println("1");
        dos.flush();
        list = (List)objectInputStream.readObject();//接受数据库传来的表
        System.out.println(list);
    }


    public void userChooseCourse(String sid, String cid)throws Exception{
        dos.println("2");
        dos.flush();

        dis.readLine();

        dos.println(sid);
        dos.flush();

        dis.readLine();


        dos.println(cid);
        dos.flush();
        dis.readLine();

        System.out.println(dis.readLine());

    }
    public void deleteSelectedCourse(String sid, String cid)throws Exception{
        dos.println("3");
        dos.flush();
        dis.readLine();

        dos.println(sid);
        dos.flush();
        dis.readLine();

        dos.println(cid);
        dos.flush();
        dis.readLine();

        System.out.println(dis.readLine());
    }
    public void deleteCourse(String cid)throws Exception{
        dos.println("4");
        dos.flush();
        dis.readLine();

        dos.println(cid);
        dos.flush();
        dis.readLine();

        System.out.println(dis.readLine());
    }
    public void addCourse(String cid, String cname, String to)throws Exception{
        dos.println("5");
        dos.flush();
        dis.readLine();

        dos.println(cid);
        dos.flush();
        dis.readLine();

        dos.println(cname);
        dos.flush();
        dis.readLine();

        dos.println(to);
        dos.flush();
        dis.readLine();

        System.out.println(dis.readLine());
    }



    public void updateStudent(String op)throws Exception{
        dos.println("6");
        dos.flush();
        dis.readLine();


        dos.println(op);
        dos.flush();

        list = (List)objectInputStream.readObject();//接受数据库传来的表
        System.out.println(list);
    }

}
