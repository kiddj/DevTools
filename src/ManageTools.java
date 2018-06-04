public class ManageTools {

    private static void getInstalledTools(){
        Sysinfo SysUser = new Sysinfo();
        Sysinfo.readInfo();
    }

    public static void SearchAdd(){
        getInstalledTools();
    }


}