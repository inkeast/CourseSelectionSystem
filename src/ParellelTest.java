import java.util.Random;

class ThreadChooseCourse extends Thread{
    private int port;
    private String host;
    ThreadChooseCourse(int port, String host){
        this.port = port;
        this.host = host;
    }

    public void run() {
        int count = 20;

            try {
                ClientSocket UserService = new ClientSocket(host, port);
                while (count--!=0) {
                    String sid = "" + (new Random().nextInt(100)+1);
                    String cid = "" + (new Random().nextInt(3)+1);
                    UserService.userChooseCourse(sid, cid);}
            } catch (Exception io) {
                io.printStackTrace();
            }

    }

    public void selectInfo() throws Exception{
        ClientSocket UserService = new ClientSocket(host,port);
        UserService.updateStudent("select * from infor ;");
    }

}

public class ParellelTest extends Thread{


    public static void main(String[] args) throws Exception{
        ThreadChooseCourse threads[] = new ThreadChooseCourse[100];

        for(int i = 0; i < 100; i++){
            threads[i] = new ThreadChooseCourse(9999,"127.0.0.1");
            threads[i].start();
            //int n = new Random().nextInt(10);
            //System.out.println(n);
        }
        ThreadChooseCourse threads2 = new ThreadChooseCourse(9999,"127.0.0.1");
        threads2.selectInfo();


        //ThreadChooseCourse threads1 = new ThreadChooseCourse(9999,"127.0.0.1");
        //ThreadChooseCourse threads2 = new ThreadChooseCourse(9999,"127.0.0.1");


        //threads1.start();
        //threads2.start();
    }


}
