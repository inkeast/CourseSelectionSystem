public class Test {
    public static void main(String[] args) {
        Operating operating = new Operating();
        operating.dbms();
        //operating.init();
       // System.out.println( operating.dbms_online("select * from course where cname = 体育 ;"));
        //operating.dbms_online("insert into infor ( SID , CID , time ) values ( 8 , 99 , 2019-07-09 11:50:01 ) ;");

       //System.out.println( operating.dbms_online("select * from course ;"));
        //System.out.println( operating.dbms_online("update course set renum = renum - 1 , penum = penum + 1 where CID = 1 ;"));
        //System.out.println( operating.dbms_online("select * from course ;"));
       /* System.out.println( operating.dbms_online("update course set renum = renum-1 , penum=penum+1 where CID = 1 ;"));
        System.out.println( operating.dbms_online("select * from course ;"));*/

    }
}