import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class client {

    public  static void  send(Socket s)
    {
        try{  PrintWriter pw=new PrintWriter(s.getOutputStream(),true);//IO流发送
            Scanner in=new Scanner(System.in);
            String i=in.nextLine();
            pw.println(i);
        }catch(Exception e){

        }
    }
    public  static void  receive(Socket s)throws Exception
    {    List list=null;
        ObjectInputStream objectInputStream = new ObjectInputStream(s.getInputStream());
        try{ /*InputStream isr=s.getInputStream();//读取数据*/

            list = (List)objectInputStream.readObject();//接受数据库传来的表
            System.out.println(list);
            //输出数据
        }catch(Exception e){

        }
    }
    public static void main(String[] args){
        try{
            Socket s=new Socket("127.0.0.1",9999);//进入端口，前面是服务器的Ip，自己电脑一般是127.0.0.1,后面的是端口，与服务器对应   PrintWriter pw=new PrintWriter(s.getOutputStream(),true);//IO流发送   pw.println("我是客户端");//发送的内容
            send(s);
            receive(s);

        }catch(Exception e){

        }
    }



}
