import java.util.*;
public class Test {
    public static void main(String[] args) {
        Operating operating = new Operating();
        /*operating.dbms();*/
        operating.init();
       // System.out.println( operating.dbms_online("select * from course where cname = 体育 ;"));


       /* List list=operating.dbms_online("select * from infor ;");*/

        System.out.println(operating.dbms_online("select * from infor ;"));
        operating.dbms_online(" insert into infor(SID,CID,time) values(1,1,2019-7-8) ;");
       operating.dbms_online("delete from infor where SID = 1 and CID = 1  ;") ;
       System.out.println(operating.dbms_online(" select * from infor ;"));
       /* System.out.println(operating.dbms_online("select * from infor  ;"));
        System.out.println(operating.dbms_online("select * from course  ;"));
        //int i=(int)list.get(3);
       /* int b = Integer.valueOf(((Map)list.get(0)).get("course.renum").toString()).intValue();
        int c = Integer.valueOf(((Map)list.get(0)).get("course.penum").toString()).intValue();
       System.out.println(b);
        System.out.println(c);*/
       /* System.out.println( operating.dbms_online("update course set renum = renum-1 , penum=penum+1 where CID = 1 ;"));
        System.out.println( operating.dbms_online("select * from course ;"));*/

    }
}