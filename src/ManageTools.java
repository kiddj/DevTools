import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

public class ManageTools {

    private static Scanner input = new Scanner(System.in);
    private static ArrayList<SWinfo> saved_sw = new ArrayList<SWinfo>();

    private static void getInstalledTools(){
        Sysinfo.readInfo();
    }

    public static void LoadSavedTools(){
        saved_sw.clear();
        ResultSet rs = User.getPrograms_withid();
        try{
            while(rs.next()){
                saved_sw.add(new SWinfo(rs.getString("name"),rs.getString("version")));
            }
        } catch (Exception e){
            Cprint.e(" Error occurs: " + e);
        }
    }

    public static void ShowTools(){
        LoadSavedTools();

        TableList detail_tools = new TableList(1,User.uid + "'s Saved Tools").withUnicode(true);
        int index_sw = 0;
        try{
            for (SWinfo sw : saved_sw) {
                index_sw++;
                String dsw = String.valueOf(index_sw) + ". " + sw.name + " " + sw.version;
                detail_tools.addRow(ls(dsw,60,0));
            }
        } catch (Exception e){
            Cprint.e(" Error occurs: " + e);
        }
        if (index_sw == 0) detail_tools.addRow(ls("No saved tool. We recommend that you add a new one.",60,0));
        detail_tools.print();
    }

    private static boolean isexist(SWinfo search_sw){
        for (SWinfo sw : saved_sw){
            if (sw.name.equals(search_sw.name) && sw.version.equals(search_sw.version)) return true;
        }
        return false;
    }

    public static void SearchAdd(){
        int sel_in = 0;
        // Load tools from local
        getInstalledTools();

        while(sel_in != -1) {
            // Match with Tools in Server
            LoadSavedTools();
            ResultSet rs = User.getPrograms(null);  // get All Dev (created by admin)
            ArrayList<SWinfo> mtool = new ArrayList<SWinfo>();

            TableList match_tools = new TableList(2, "Detected Tools in your local PC", "Saved").withUnicode(true);
            int index_sw = 0;
            try {
                while (rs.next()) {
                    for (SWinfo installed_sw : Sysinfo.list_sw) {
                        SWinfo loaded_sw = new SWinfo(
                                rs.getString("name"),
                                rs.getString("version"),
                                rs.getString("insPath"),
                                rs.getString("reference"),
                                rs.getString("details")
                        );
                        if (installed_sw.name.toLowerCase().contains(loaded_sw.name.toLowerCase())) {   // Match
                            index_sw++;
                            String dsw = String.valueOf(index_sw) + ". " + rs.getString("name") + " " + rs.getString("version");
                            // Check Version
                            if (!installed_sw.version.equals(loaded_sw.version)) dsw += " (Version may not match)";
                            match_tools.addRow(ls(dsw, 55, 0), ls(isexist(loaded_sw) ? "O" : "X", 5, 0));
                            mtool.add(loaded_sw);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                Cprint.e(" Error occurs: " + e);
                Cprint.e(" Failed. Please contact system administrator");
            }
            if (index_sw == 0) match_tools.addRow(ls("We cannot find matched Tools.", 55, 0), ls("", 5, 0));
            match_tools.print();

            System.out.print(" Select Tool to Save (Exit:0) > ");
            sel_in = input.nextInt() - 1;
            if(sel_in != -1){
                try {
                    SWinfo sel_sw = mtool.get(sel_in);
                    if(isexist(sel_sw)) Cprint.w("\n [" + sel_sw.name + " " + sel_sw.version + "] is already saved.");
                    else User.addDev(sel_sw.name,sel_sw.version,sel_sw.insPath,sel_sw.reference,sel_sw.details,null);
                } catch (Exception e) {
                    Cprint.e(" Error occurs : " + e);
                    Cprint.e(" Failed. Please contact system administrator");
                }
            }
        }
    }

    private static String ls(String str, int width, int align){
        if (align == 0) return String.format("%-" + width + "." + width + "s",str);
        else return String.format("%" + width + "." + width + "s",str);
    }

}