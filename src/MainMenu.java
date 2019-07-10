import java.util.Scanner;

/**
 * 主界面
 *
 */
public class MainMenu {
    public static void main(String[] args) throws Exception {
        System.out.println("_________________________");
        System.out.println("      欢迎使用选课系统         ");
        System.out.println("    1.查询课程信息");
        System.out.println("    2.选择可选课程");
        System.out.println("    3.退掉已选课程");
        System.out.println("    4.删除课程");
        System.out.println("    5.增加课程");
        System.out.println("    6.更改学生信息");
        System.out.println("    7.退出选课系统");
        System.out.println("_________________________");

        ClientSocket UserService = new ClientSocket("127.0.0.1",9999);
        Scanner sc = new Scanner(System.in);
        while (true){
            int a = sc.nextInt();
            switch (a) {
                case 1:
                    UserService.userLookCourse();
                    break;
                case 2:
                    System.out.println("请输入SID:");
                    String sid = sc.next();
                   /* System.out.println(sid);*/

                    System.out.println("请输入CID:");
                    String cid = sc.next();
                  /*  System.out.println(cid);*/

                    UserService.userChooseCourse(sid, cid);
                    break;
                case 3:
                    System.out.println("请输入SID:");
                    String ssid = sc.next();
                    /* System.out.println(sid);*/

                    System.out.println("请输入CID:");
                    String scid = sc.next();
                    /*  System.out.println(cid);*/
                    UserService.deleteSelectedCourse(ssid, scid);
                    break;
                case 4:
                    System.out.println("请输入CID:");
                    String cou = sc.next();
                    UserService.deleteCourse(cou);
                    break;
                case 5:
                    System.out.println("请输入CID, CNAME, TOLNUM:");
                    String c = sc.next();
                    String cname = sc.next();
                    String to = sc.next();
                    UserService.addCourse(c, cname, to);
                    break;
                case 6:
                    System.out.println("请输入sql:");
                    String k=sc.nextLine();
                    String op = sc.nextLine();
                    System.out.println(op);
                    UserService.updateStudent(op);
                    break;
                case 7:
                    System.out.println("用户成功退出！");
                    System.exit(0);
                    break;
                default:
                    System.out.println("输入数字不合法，重新输入");

            }

        }

    }
    void inputChoose(){
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入SID:");
        String sid = sc.nextLine();
        System.out.println(sid);

        System.out.print("请输入CID:");
        String cid = sc.nextLine();
        System.out.println(cid);
    }




}


