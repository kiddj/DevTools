import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.Color.RED;
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

    private static boolean isexist_server(SWinfo search_sw){
        for (SWinfo sw : saved_sw){
            if (sw.name.equals(search_sw.name) && sw.version.equals(search_sw.version)) return true;
        }
        return false;
    }

    private static void printTool(SWinfo sw){
        TableList tinfo = new TableList(1, sw.name).withUnicode(true);
        tinfo.addRow(ls("Version : " + sw.version, 60, 0));
        tinfo.addRow(ls("Install Path : " + sw.insPath, 60, 0));
        tinfo.addRow(ls("Reference : " + sw.reference, 60, 0));
        tinfo.addRow(ls("Details : " + sw.details, 60, 0));
        System.out.println();
        tinfo.print();
    }

    public static void Restore(){
        int sel_in = 0;
        // Load tools from local
        getInstalledTools();

        while(sel_in != -1) {

            // Match with Tools in Server
            LoadSavedTools();
            ResultSet rs = User.getPrograms_withid();  // get All Dev (created by admin)
            ArrayList<SWinfo> rtool = new ArrayList<SWinfo>();
            ArrayList<SWinfo> sw_unins = new ArrayList<SWinfo>();
            ArrayList<SWinfo> sw_ins = new ArrayList<SWinfo>();

            TableList match_tools = new TableList(2, "Saved Tools", "Installed Tools").withUnicode(true);
            int index_sw = 0;
            int installed_count = 0;
            try {
                while (rs.next()) {
                    SWinfo loaded_sw = new SWinfo(
                            rs.getString("name"),
                            rs.getString("version"),
                            rs.getString("insPath"),
                            rs.getString("reference"),
                            rs.getString("details")
                    );
                    index_sw++;
                    installed_count = 0;
                    String dsw_server = String.valueOf(index_sw) + ". " + loaded_sw.name + " " + loaded_sw.version;
                    for (SWinfo installed_sw : Sysinfo.local_sw) {
                        if (installed_sw.name.toLowerCase().contains(loaded_sw.name.toLowerCase())) {   // Match (User save <-> Local)
                            String dsw_local = installed_sw.name + " " + installed_sw.version;
                            match_tools.addRow(ls((installed_count==0)?dsw_server:"", 20, 0), ls(dsw_local, 40, 0));
                            if(installed_count == 0) sw_ins.add(loaded_sw);
                            installed_count++;
                        }
                    }
                    if (installed_count == 0){
                        match_tools.addRow(ls(dsw_server, 20, 0), ls("", 40, 1));
                        sw_unins.add(loaded_sw);
                    }
                    rtool.add(loaded_sw);
                }
            } catch (Exception e) {
                Cprint.e(" Error occurs: " + e);
                Cprint.e(" Failed. Please contact system administrator");
            }
            if (index_sw == 0) match_tools.addRow(ls("No Tool saved in Server", 20, 0), ls("", 40, 0));
            match_tools.print();

            //Notice
            for (SWinfo sw : sw_ins){
                Cprint.i(" [" + sw.name + " " + sw.version + "] Tool seems to be installed.");
            }
            for (SWinfo sw : sw_unins){
                Cprint.e(" [" + sw.name + " " + sw.version + "] Tool was not found in your environment.");
            }
            System.out.println();

            System.out.print(" Select Tool to Restore (Exit:0 / Reload Local Tool:-1) > ");
            sel_in = input.nextInt() - 1;
            input.nextLine();
            if(sel_in != -1){
                if(sel_in == -2){
                    getInstalledTools();
                    sel_in = 0;
                    break;
                }
                try {
                    SWinfo sel_sw = rtool.get(sel_in);
                    printTool(sel_sw);
                    System.out.print(" Redirect to installation site (y.n) : ");
                    String cont = input.nextLine().toLowerCase();
                    if (cont.equals("y")){
                        open_web(sel_sw.insPath);
                    }
                } catch (Exception e) {
                    Cprint.e(" Error occurs : " + e);
                    Cprint.e(" Failed. Please contact system administrator");
                }
            }
        }
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
                    for (SWinfo installed_sw : Sysinfo.local_sw) {
                        SWinfo loaded_sw = new SWinfo(
                                rs.getString("name"),
                                rs.getString("version"),
                                rs.getString("insPath"),
                                rs.getString("reference"),
                                rs.getString("details")
                        );
                        if (installed_sw.name.toLowerCase().contains(loaded_sw.name.toLowerCase())) {   // Match
                            index_sw++;
                            String dsw = String.valueOf(index_sw) + ". " + loaded_sw.name + " " + loaded_sw.version;
                            // Check Version
                            if (!installed_sw.version.equals(loaded_sw.version)) dsw += " (Version may not match)";
                            match_tools.addRow(ls(dsw, 55, 0), ls(isexist_server(loaded_sw) ? "O" : "X", 5, 0));
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
                    if(isexist_server(sel_sw)) Cprint.w("\n [" + sel_sw.name + " " + sel_sw.version + "] is already saved.");
                    else User.addDev(sel_sw.name,sel_sw.version,sel_sw.insPath,sel_sw.reference,sel_sw.details,null);
                } catch (Exception e) {
                    Cprint.e(" Error occurs : " + e);
                    Cprint.e(" Failed. Please contact system administrator");
                }
            }
        }
    }

    private static void open_web(String url){
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static String ls(String str, int width, int align){
        if (align == 0) return String.format("%-" + width + "." + width + "s",str);
        else return String.format("%" + width + "." + width + "s",str);
    }

}