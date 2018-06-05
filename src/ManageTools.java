import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

public class ManageTools {

    private static Sysinfo SysUser = new Sysinfo();
    private static Scanner input = new Scanner(System.in);

    private static void getInstalledTools(){
        Sysinfo.readInfo();
    }

    public static void SearchAdd(){
        getInstalledTools();
    }

}