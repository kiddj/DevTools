import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

public class ManageTools {

    private static Scanner input = new Scanner(System.in);

    private static void getInstalledTools(){
        Sysinfo.readInfo();
    }

    public static void SearchAdd(){
        getInstalledTools();

        // Match with Tools in Server
        ResultSet rs = User.getPrograms(null);

        TableList detail_tp = new TableList(2,"Tools in your local PC","Saved").withUnicode(true);
        int index_sw = 0;
        try{
            while(rs.next()){
                for(SWinfo installed_sw : Sysinfo.list_sw){
                    String loaded_sw = rs.getString("name");
                    String v_load_sw = rs.getString("version");
                    if (installed_sw.name.toLowerCase().contains(loaded_sw.toLowerCase())) {
                        index_sw++;
                        String dsw = String.valueOf(index_sw) + ". " + rs.getString("name") + " " + rs.getString("version");
                        // Check Version
                        if (!installed_sw.version.equals(v_load_sw)) dsw += " (Maybe different version)";
                        detail_tp.addRow(ls(dsw,55,0),ls("no",5,0));
                        break;
                    }
                }
            }
        } catch (Exception e){
            Cprint.e(" Error occurs" + e);
        }
        if (index_sw == 0) detail_tp.addRow(ls("We cannot find matched Tools.",55,0),ls("",5,0));
        detail_tp.print();
    }

    private static String ls(String str, int width, int align){
        if (align == 0) return String.format("%-" + width + "." + width + "s",str);
        else return String.format("%" + width + "." + width + "s",str);
    }

}