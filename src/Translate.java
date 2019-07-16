public class Translate //将接收到的客户端指令转化成 对应的SQL语句
        /*
         * 1.查询课程   1_1 查询全部课程信息
         *              1_2/3  根据ID或者名称查询单条课程信息
         * 先学号登陆
         * 2.选课      2
         * 3.退课
         * */ {
    public static String selectcourse()  //生成查询全部课程信息的SQL语句
    {
        String translate = new String("select * from course ;");
        return translate;
    }

    public static String find_c_byname(String value)//生成根据课程名称查询的SQL语句
    {
        String translate = new String("select * from course where course.cname = " + value + " ;");
        return translate;
    }

    public static String find_c_byid(String value)//生成根据ID查询课程的SQL语句
    {
        String translate = new String("select * from course where course.CID = " + value + " ;");

        return translate;
    }

    public static String find_s_byid(String value)//生成根据学生ID查
    {
        String translate = new String("select * from student where student.SID = " + value + " ;");

        return translate;
    }

    public  static String course_renum(String value)//判断课余量大于0
    {
        String translate = new String("select * from course where course.CID = " + value + " and renum > 0 ;");

        return translate;
    }
    public  static String penum_0(String value)//判断penum为零
    {
        String translate = new String("select * from course where course.CID = " + value + " and penum = 0 ;");

        return translate;
    }

    public static String add_infor(String sid, String cid, String time)//生成  向选课记录中插入一条信息的  SQL语句

    {
        String translate = new String("insert into infor(SID,CID,time) values(" + sid + "," + cid + "," + time + ") ;");

        return translate;

    }


    public static String delete_infor(String sid, String cid)// 生成退课语句
    {
        String translate = new String("delete from infor where SID = " + sid + " and CID = " + cid + " ;");

        return translate;
    }
    public static String find_infor(String sid, String cid)// 查询是否有选课信息
    {
        String translate = new String("select * from infor where SID = " + sid + " and CID = " + cid + " ;");

        return translate;
    }

    public static String delete_course(String cid)// 生成删课语句
    {
        String translate = new String("delete from course where CID = " + cid + " ;");

        return translate;
    }

    public static String addcourse(String cid, String cname, String tolnum)// 生成增课语句
    {
        String translate = new String("insert into course(CID,cname,tolnum,renum,penum) values(" + cid + "," + cname + "," + tolnum + "," + tolnum + "," + "0) ;");

        return translate;
    }

    public  static String renum_change(String cid,int renum,int penum)// 课余量更改
    {
        String translate = new String("update course set renum = "+renum+" , penum = "+penum+" where CID = " + cid + " ;");

        return translate;
    }




    public static void main(String[] args) {


    }
}


