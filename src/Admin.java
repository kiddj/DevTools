import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

public class Admin {

    private static Scanner input = new Scanner(System.in);

    public static void searchTools(String type){
        String type_l = type.toLowerCase();
        ResultSet rs = null;
        if(type_l.equals("uid")) type = "ID";
        try{
            if(type_l.equals("name") || type_l.equals("uid")){
                System.out.print("Enter " + type + " to Search: ");
                rs = User.getTools(type_l, input.nextLine());
            } else{
                if(type_l.equals("all")) rs = User.getAllPrograms();
                else{
                    rs = User.getPrograms_withid();
                }
            }
            
            while(rs.next()){
                    System.out.println(rs.getString("name") + " " + rs.getString("version"));
            }
        } catch(Exception e){

        }
    }

    public static void deleteTemplate(){
        try{
        ResultSet templates = User.getAdminTemplates();
        ArrayList<String> temps = new ArrayList<String>();
        if(templates != null){
            TableList list_tp = new TableList(2, "Name","Description").withUnicode(true);

            int index = 1;
            while(templates.next()){
               String details = templates.getString("details");
                if(details == null) details = "";
                list_tp.addRow(String.valueOf(index) + ". " + ls(templates.getString("name"),15,0),ls(details,50,0));
                index++;
                temps.add(templates.getString("name"));
            }
            list_tp.print();
            System.out.print(" Select Template (Exit:0) > ");

            int sel_temp = input.nextInt() - 1;
            input.nextLine();
            if (sel_temp == -1) return;
            String temp = temps.get(sel_temp);

            if(User.deleteTemplate(temp)) Cprint.e(" [" + temp + "] template deleted.");
        } else{
            Cprint.e(" There is no template available :(");
        }
        } catch(Exception e){
        }
    }

    public static void deleteProgram(){
        try{
        ResultSet templates = User.getAdminTemplates();
        ArrayList<String> temps = new ArrayList<String>();
        ArrayList<SWinfo> programs = new ArrayList<SWinfo>();
        if(templates != null){
            TableList list_tp = new TableList(2, "Name","Description").withUnicode(true);

            int index = 1;
            while(templates.next()){
                String details = templates.getString("details");
                if(details == null) details = "";
                list_tp.addRow(String.valueOf(index) + ". " + ls(templates.getString("name"),15,0),ls(details,50,0));
                index++;
                temps.add(templates.getString("name"));
            }
            list_tp.print();
            System.out.print(" Select Template > ");

            String temp = temps.get(input.nextInt()-1);
            input.nextLine();

            ResultSet rs = User.getPrograms(temp);
            
            index = 1;
            list_tp = new TableList(2, "Name","Description").withUnicode(true);
            while(rs.next()){
                 list_tp.addRow(String.valueOf(index) + ". " + ls(rs.getString("name"),15,0),ls(rs.getString("version"),50,0));
                 programs.add(new SWinfo(rs.getString("name"),rs.getString("version")));
                 index++;
            }
            list_tp.print();
            System.out.print(" Select Program > ");

            SWinfo program = programs.get(input.nextInt()-1);
            input.nextLine();
            if(User.deleteProgram(program,temp)) Cprint.e(" [" + program.name + " " + program.version + "] deleted from " + temp + " template.");
        } else{
            Cprint.e(" There is no template available :(");
        }
        } catch(Exception e){
        }
    }

    public static void addProgram() {
        try{
                System.out.print(" Name: ");
                String name = input.nextLine();
                System.out.print(" Version: ");
                String version = input.nextLine();
                System.out.print(" Download URL: ");
                String insPath = input.nextLine();
                System.out.print(" Reference: ");
                String reference = input.nextLine();
                System.out.print(" Details: ");
                String details = input.nextLine();

                User.addDev(name,version,insPath,reference,details, null);
        } catch(Exception e){
        }
    }
    public static void addProgramToTemplate(){
        try{
        ResultSet templates = User.getAdminTemplates();
        ArrayList<String> temps = new ArrayList<String>();
        ArrayList<SWinfo> programs = new ArrayList<SWinfo>();
        if(templates != null){
            TableList list_tp = new TableList(2, "Name","Description").withUnicode(true);

            ResultSet rs = User.getPrograms_withid();
            
            int index = 1;
            while(rs.next()){
                 list_tp.addRow(String.valueOf(index) + ". " + ls(rs.getString("name"),15,0),ls(rs.getString("version"),50,0));
                 programs.add(new SWinfo(rs.getString("name"),rs.getString("version")));
                 index++;
            }
            list_tp.print();
            System.out.print(" Select Program > ");
            SWinfo program = programs.get(input.nextInt()-1);
            input.nextLine();

            index = 1;
            list_tp = new TableList(2, "Name","Description").withUnicode(true);
            while(templates.next()){
                String details = templates.getString("details");
                if(details == null) details = "";
                list_tp.addRow(String.valueOf(index) + ". " + ls(templates.getString("name"),15,0),ls(details,50,0));
                index++;
                temps.add(templates.getString("name"));
            }
            list_tp.print();
            System.out.print(" Select Template > ");

            String temp = temps.get(input.nextInt()-1);
            input.nextLine();

            if(User.addProgramToTemplate(program,temp)) Cprint.w(" [" + program.name + " " + program.version + " added to " + temp + " template.");
        } else{
            Cprint.e(" There is no template available :(");
        }
        } catch(Exception e){
        }
    }
    public static void checkProgram(){
        try{
            ResultSet templates = User.getAdminTemplates();
            ArrayList<String> temps = new ArrayList<String>();
            if(templates != null){
                TableList list_tp = new TableList(2, "Name","Description").withUnicode(true);

                int index = 1;
                while(templates.next()){
                    String details = templates.getString("details");
                    if(details == null) details = "";
                    list_tp.addRow(String.valueOf(index) + ". " + ls(templates.getString("name"),15,0),ls(details,50,0));
                    index++;
                    temps.add(templates.getString("name"));
                }
                list_tp.print();
                System.out.print(" Select Template > ");

                String temp = temps.get(input.nextInt()-1);
                input.nextLine();

                ResultSet rs = User.getPrograms(temp);

                TableList detail_tp = new TableList(1,temp).withUnicode(true);
                int index_sw = 0;
                try{
                    while(rs.next()){
                        index_sw++;
                        String dsw = String.valueOf(index_sw) + ". " + rs.getString("name") + " " + rs.getString("version")  + "(" + rs.getString("insPath") + ")";
                        detail_tp.addRow(ls(dsw,70,0));
                    }
                } catch (Exception e){
                    Cprint.e(" Error occurs" + e);
                }
                if (index_sw == 0) detail_tp.addRow(ls("Tool does not exist",70,0));
                detail_tp.print();
            } else{
                Cprint.e(" There is no template available :(");
            }
        } catch (Exception e){

        }
    }

    private static String ls(String str, int width, int align){
        if (align == 0) return String.format("%-" + width + "." + width + "s",str);
        else return String.format("%" + width + "." + width + "s",str);
    }

}
