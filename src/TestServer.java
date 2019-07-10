
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
    public class  TestServer {

        public static  String receive(Socket s) throws Exception{//从客户端接收消息

            InputStreamReader isr=new InputStreamReader(s.getInputStream());//接收客户端的数据
            BufferedReader br=new BufferedReader(isr);//存入缓存
            String xinxi=br.readLine();//读出
            return xinxi;

        }


        public static void send(String message,Socket s)throws Exception//向客户端发送消息
        {
            PrintWriter pw=new PrintWriter(s.getOutputStream(),true);//向客户端传数据
            pw.println(message);//数据是啥

        }


        public static void  main(String[] args)throws Exception{



            Socket socket = new Socket("127.0.0.1",2048);//链接数据库
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            Translate c=new Translate();
            List list=null;
            ServerSocket ss=new ServerSocket(9999);//设置服务器的端口  链接客户端
            Socket s=ss.accept();
            ObjectOutputStream objectOutputStream_c = new ObjectOutputStream(s.getOutputStream());//客户端通道
           while(true) {
               String a = receive(s);

               switch (a) {
                   case "1": {//查询课程信息

                       dataOutputStream.writeUTF(c.selectcourse() + "\n");//向数据库发送查询信息
                       list = (List) objectInputStream.readObject();//接受数据库传来的表

                       objectOutputStream_c.writeUnshared(list);//向客户端发送表
                       objectOutputStream_c.flush();

                       break;
                   }
                   case "2": {
                       send("success1", s);

                       String sid = receive(s);
                       send("success2", s);//选课

                       String cid = receive(s);
                       send("success3", s);

                       dataOutputStream.writeUTF(c.find_s_byid(sid) + "\n");//先查学生是否存在

                       list = (List) objectInputStream.readObject();
                       if (list.size()==0) {
                           send("学生不存在", s);
                           break;
                       }
                       ;

                       dataOutputStream.writeUTF(c.find_c_byid(cid) + "\n");//查课程是否存在
                       list = (List) objectInputStream.readObject();
                       if (list.size()==0) {
                           System.out.println("课程不存在");
                           send("课程不存在", s);
                           break;
                       }
                       ;

                       dataOutputStream.writeUTF(c.course_renum(cid) + "\n");//查课余量不为0
                       list = (List) objectInputStream.readObject();
                       if (list.size()==0) {
                           System.out.println("课余量不足");
                           send("课余量不足", s);
                           break;
                       }
                       ;
                       dataOutputStream.writeUTF(c.find_infor(sid,cid) + "\n");//查该条选课信息不存在
                       list = (List) objectInputStream.readObject();
                       if (list.size()!=0) {
                           send("该选课信息已存在", s);
                           break;
                       }


                       SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");//设置日期格式
                       String time = df.format(new Date());// new Date()为获取当前系统时间
                       dataOutputStream.writeUTF(c.add_infor(sid, cid, time)+"\n");//存入选课信息
                       list = (List) objectInputStream.readObject();
                       dataOutputStream.writeUTF(c.renum_reduce(cid)+"\n");//修改课余量
                       list = (List) objectInputStream.readObject();
                       send("选课成功", s);
                       break;
                   }
                   case "3": {
                       send("success1", s);
                       String sid = receive(s);
                       send("success", s);//退课
                       String cid = receive(s);
                       send("success", s);

                       dataOutputStream.writeUTF(c.find_s_byid(sid) + "\n");//先查学生是否存在
                       list = (List) objectInputStream.readObject();

                       if (list.size()==0) {
                           send("学生不存在", s);
                           break;
                       }
                       ;System.out.println(1);

                       dataOutputStream.writeUTF(c.find_c_byid(cid) + "\n");//查课程是否存在
                       list = (List) objectInputStream.readObject();
                       if (list.size()==0) {
                           send("课程不存在", s);
                           break;
                       }
                       System.out.println(2);
                       ;

                       dataOutputStream.writeUTF(c.find_infor(sid,cid) + "\n");//查选课信息是否存在

                       list = (List) objectInputStream.readObject();

                       if (list.size()==0) {
                           send("该选课信息不存在", s);
                           break;
                       }

                       dataOutputStream.writeUTF(c.delete_infor(sid, cid)+ "\n");//删去选课信息
                       list = (List) objectInputStream.readObject();
                       dataOutputStream.writeUTF(c.renum_incrase(cid)+ "\n");//修改课余量
                       list = (List) objectInputStream.readObject();
                       send("退课成功", s);
                       break;
                   }
                   case "4": {
                       send("success1", s);
                       String cid = receive(s);
                       send("success", s);//删课

                       dataOutputStream.writeUTF(c.find_c_byid(cid) + "\n");//查课程是否存在
                       list = (List) objectInputStream.readObject();
                       if (list.size()==0) {
                           send("课程不存在", s);
                           break;
                       }
                       ;

                       dataOutputStream.writeUTF(c.penum_0(cid) + "\n");//查penum 为0
                       list = (List) objectInputStream.readObject();
                       if (list.size()==1) {//可以删课
                           dataOutputStream.writeUTF(c.delete_course(cid) + "\n");
                           list = (List) objectInputStream.readObject();
                           send("删课成功", s);
                           break;
                       } else {
                           send("选课人数不为0,无法删除", s);
                           break;
                       }
                   }
                   case "5": {//增加课程
                       send("success1", s);

                       String cid = receive(s);
                       send("success", s);
                       String cname = receive(s);
                       send("success", s);
                       String tolnum = receive(s);
                       send("success", s);

                       dataOutputStream.writeUTF(c.find_c_byid(cid) + "\n");//查课程是否存在

                       list = (List) objectInputStream.readObject();

                       if (list.size()!=0) {
                           send("课程ID已存在", s);
                           break;
                       }

                       dataOutputStream.writeUTF(c.find_c_byname(cname) + "\n");//查课程是否存在

                       list = (List) objectInputStream.readObject();
                       System.out.println(list);
                       if (list.size()!=0) {
                           send("课程名已存在", s);
                           break;
                       }

                       dataOutputStream.writeUTF(c.addcourse(cid, cname, tolnum) + "\n");//增课
                       list = (List) objectInputStream.readObject();
                       send("增课成功", s);
                       break;
                   }
                   case "6": {
                       send("success1", s);
                       String sql=receive(s);
                       System.out.println(sql);
                       dataOutputStream.writeUTF(sql + "\n");

                       list = (List) objectInputStream.readObject();
                       System.out.println(list);
                       objectOutputStream_c.writeUnshared(list);//向客户端发送表
                       objectOutputStream_c.flush();
                       break;

                   }
               }

           }
        }
    }


