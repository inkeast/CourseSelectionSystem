
import java.io.*;
import java.lang.reflect.Executable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class  TestServer extends Thread{
    private static ServerSocket ss;
    public Socket s;
    private static String ip=null;
    private static int serverport;
    private static int databaseport;
    public static void readsocket()
    {
       try{ File file=new File(".\\socket.txt");
        InputStreamReader read = new InputStreamReader(
                new FileInputStream(file),"utf-8");
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt;
        while((lineTxt = bufferedReader.readLine()) != null){
            if(lineTxt.equals("serverport:")){serverport=Integer.parseInt(bufferedReader.readLine());}
            if(lineTxt.equals("databaseport:")){databaseport=Integer.parseInt(bufferedReader.readLine());}
            if(lineTxt.equals("ip:")){ip=bufferedReader.readLine();}

        }
        read.close();}catch (Exception e){};
    }


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
        //选课
        public synchronized static void choose_course(Socket s,DataOutputStream dataOutputStream,ObjectInputStream objectInputStream)throws Exception
    {   List list=null;
        Translate c=new Translate();
        send("success1", s);

        String sid = receive(s);
        send("success2", s);//选课

        String cid = receive(s);
        send("success3", s);


        dataOutputStream.writeUTF(c.find_s_byid(sid) + "\n");//先查学生是否存在

        list = (List) objectInputStream.readObject();
        if (list.size()==0) {
            send("学生不存在", s);
            return;
        }
        ;

        dataOutputStream.writeUTF(c.find_c_byid(cid) + "\n");//查课程是否存在
        list = (List) objectInputStream.readObject();
        if (list.size()==0) {

            send("课程不存在", s);
            return;
        }
        ;

        dataOutputStream.writeUTF(c.course_renum(cid) + "\n");//查课余量不为0
        list = (List) objectInputStream.readObject();
        if (list.size()==0) {
            send("课余量不足", s);
            return;
        }
        ;
        dataOutputStream.writeUTF(c.find_infor(sid,cid) + "\n");//查该条选课信息不存在
        list = (List) objectInputStream.readObject();
        if (list.size()!=0) {
            send("该选课信息已存在", s);
            return;
        }


        //修改renum
        dataOutputStream.writeUTF(c.find_c_byid(cid)+"\n");
        list = (List) objectInputStream.readObject();
        int r = Integer.valueOf(((Map)list.get(0)).get("course.renum").toString()).intValue();
        int p = Integer.valueOf(((Map)list.get(0)).get("course.penum").toString()).intValue();
        dataOutputStream.writeUTF(c.renum_change(cid,r-1,p+1)+"\n");//修改课余量
        list = (List) objectInputStream.readObject();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");//设置日期格式
        String time = df.format(new Date());// new Date()为获取当前系统时间
        dataOutputStream.writeUTF(c.add_infor(sid, cid, time)+"\n");//存入选课信息
        list = (List) objectInputStream.readObject();
        send("选课成功", s);

    }
        //退课
        public synchronized static void abandon_course(Socket s,DataOutputStream dataOutputStream,ObjectInputStream objectInputStream)throws Exception
    {
        List list=null;
        Translate c=new Translate();
        send("success1", s);
        String sid = receive(s);
        send("success", s);//退课
        String cid = receive(s);
        send("success", s);

        dataOutputStream.writeUTF(c.find_s_byid(sid) + "\n");//先查学生是否存在
        list = (List) objectInputStream.readObject();

        if (list.size()==0) {
            send("学生不存在", s);
            return;
        }
        ;

        dataOutputStream.writeUTF(c.find_c_byid(cid) + "\n");//查课程是否存在
        list = (List) objectInputStream.readObject();
        if (list.size()==0) {
            send("课程不存在", s);
            return;
        }
        ;

        dataOutputStream.writeUTF(c.find_infor(sid,cid) + "\n");//查选课信息是否存在

        list = (List) objectInputStream.readObject();

        if (list.size()==0) {
            send("该选课信息不存在", s);
            return;
        }


        //修改课余量
        dataOutputStream.writeUTF(c.find_c_byid(cid)+"\n");
        list = (List) objectInputStream.readObject();
        int r = Integer.valueOf(((Map)list.get(0)).get("course.renum").toString()).intValue();
        int p = Integer.valueOf(((Map)list.get(0)).get("course.penum").toString()).intValue();
        dataOutputStream.writeUTF(c.renum_change(cid,r+1,p-1)+"\n");//修改课余量
        list = (List) objectInputStream.readObject();

        dataOutputStream.writeUTF(c.delete_infor(sid, cid)+ "\n");//删去选课信息
        list = (List) objectInputStream.readObject();

        send("退课成功", s);}
        //删课
        public synchronized static void remove_course(Socket s,DataOutputStream dataOutputStream,ObjectInputStream objectInputStream)throws Exception

    {
        List list=null;
        Translate c=new Translate();
        send("success1", s);
        String cid = receive(s);
        send("success", s);//删课

        dataOutputStream.writeUTF(c.find_c_byid(cid) + "\n");//查课程是否存在
        list = (List) objectInputStream.readObject();
        if (list.size()==0) {
            send("课程不存在", s);
            return;
        }
        ;

        dataOutputStream.writeUTF(c.penum_0(cid) + "\n");//查penum 为0
        list = (List) objectInputStream.readObject();
        if (list.size()==1) {//可以删课
            dataOutputStream.writeUTF(c.delete_course(cid) + "\n");
            list = (List) objectInputStream.readObject();
            send("删课成功", s);
            return;
        } else {
            send("选课人数不为0,无法删除", s);
            return;
        }}


        public  void  run(){
          try{
           System.out.println(getName());
           Socket socket = new Socket(ip,databaseport);//链接数据库
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            Translate c=new Translate();
            List list=null;
           // ServerSocket ss=new ServerSocket(9999);//设置服务器的端口  链接客户端
            ObjectOutputStream objectOutputStream_c = new ObjectOutputStream(s.getOutputStream());//客户端*/

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
                   case "2": {//选课
                      choose_course(s,dataOutputStream,objectInputStream );
                       break;
                   }
                   case "3": {//退课
                       abandon_course(s,dataOutputStream,objectInputStream);
                       break;
                   }
                   case "4": {//删课
                       remove_course(s,dataOutputStream,objectInputStream);
                       break;

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
                       dataOutputStream.writeUTF(sql + "\n");

                       list = (List) objectInputStream.readObject();
                       objectOutputStream_c.writeUnshared(list);//向客户端发送表
                       objectOutputStream_c.flush();
                       if(list.get(0).equals("exit")){System.exit(0);}
                       break;

                   }
               }

           }}catch(Exception e){};
       }
    public static void  main(String[] args)throws Exception{
           // ExecutorService pool = Executors.newFixedThreadPool(8);
        readsocket();
        ss=new ServerSocket(serverport);//设置服务器的端口  链接客户端
        while (true){
            TestServer ts =new TestServer();
            ts.s = ss.accept();
            ts.start();
            //pool.submit()
            //sleep(1);
            }

        }
    }



