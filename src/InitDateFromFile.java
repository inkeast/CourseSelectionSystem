import java.io.*;
import java.util.List;

public class InitDateFromFile {
    private static String path = "Initcmd.txt";
    public static void main(String avgs[])throws Exception{
        Operating operating = new Operating();
        operating.init();
        File file = new File(path);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String string = null;
        List list =null;
        while ((string = bufferedReader.readLine())!=null){
            System.out.println(string);
            operating.dbms_online(string);
        }
    }
}
