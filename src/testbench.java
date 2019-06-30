import java.util.List;

public class testbench {
    public static void main(String[] args){
        Operating operating = new Operating();
        String str = "select * from course";
        operating.init();
        List result = operating.dbms_online(str);
        System.out.println(result);
    }
}
