import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

public class ManageTools {

    private static Scanner input = new Scanner(System.in);

    private static void getInstalledTools(){
        Sysinfo.readInfo();
    }

    public static void ShowTools(){
        ResultSet rs = User.getPrograms(User.uid);

        TableList detail_tools = new TableList(1,User.uid + "'s Template").withUnicode(true);
        int index_sw = 0;
        try{
            while(rs.next()){
                String dsw = String.valueOf(index_sw) + ". " + rs.getString("name") + " " + rs.getString("version");
                detail_tools.addRow(ls(dsw,60,0));
            }
        } catch (Exception e){
            Cprint.e(" Error occurs" + e);
        }
        if (index_sw == 0) detail_tools.addRow(ls("No saved tool. We recommend that you add a new one.",60,0));
        detail_tools.print();
    }

    public static void SearchAdd(){
        getInstalledTools();

        // Match with Tools in Server
        ResultSet rs = User.getPrograms(null);
        ArrayList<SWinfo> mtool = new ArrayList<SWinfo>();

        TableList match_tools = new TableList(2,"Tools in your local PC","Saved").withUnicode(true);
        int index_sw = 0;
        try{
            while(rs.next()){
                for(SWinfo installed_sw : Sysinfo.list_sw){
                    SWinfo loaded_sw = new SWinfo(
                            rs.getString("name"),
                            rs.getString("version"),
                            rs.getString("insPath"),
                            rs.getString("reference"),
                            rs.getString("details"),
                            rs.getString("template")
                            );
                    if (installed_sw.name.toLowerCase().contains(loaded_sw.name.toLowerCase())) {
                        index_sw++;
                        String dsw = String.valueOf(index_sw) + ". " + rs.getString("name") + " " + rs.getString("version");
                        // Check Version
                        if (!installed_sw.version.equals(loaded_sw.version)) dsw += " (Maybe different version)";
                        match_tools.addRow(ls(dsw,55,0),ls("no",5,0));
                        mtool.add(loaded_sw);
                        break;
                    }
                }
            }
        } catch (Exception e){
            Cprint.e(" Error occurs" + e);
        }
        if (index_sw == 0) match_tools.addRow(ls("We cannot find matched Tools.",55,0),ls("",5,0));
        match_tools.print();

        Cprint.b(" Do you want to add a new tool to the server? (y,n): ","");
        String cfm = input.nextLine().toLowerCase();
        while(cfm.equals("y")) {
            System.out.print(" Select Tool > ");

            try {
                int sel_in = input.nextInt() - 1;
                input.nextLine();
                SWinfo sel_sw = mtool.get(sel_in);
                User.addDev(sel_sw.name,sel_sw.version,sel_sw.insPath,sel_sw.reference,sel_sw.details,null);
            } catch (Exception e) {
                Cprint.e(" Error occurs : " + e);
            }

            Cprint.b(" Do you want to add a new tool to the server? (y,n): ","");
            cfm = input.nextLine().toLowerCase();
        }
    }

    private static String ls(String str, int width, int align){
        if (align == 0) return String.format("%-" + width + "." + width + "s",str);
        else return String.format("%" + width + "." + width + "s",str);
    }

}