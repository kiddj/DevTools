import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

public class Admin {

    private static Scanner input = new Scanner(System.in);

    public static void addProgram() {
        try{
            ResultSet templates = User.getAdminTemplates();
            ArrayList<String> temps = new ArrayList<String>();
            if(templates != null){
                TableList list_tp = new TableList(2, "Name","Description").withUnicode(true);

                int index = 1;
                while(templates.next()){
                    list_tp.addRow(String.valueOf(index) + ". " + ls(templates.getString("name"),15,0),ls(templates.getString("details"),50,0));
                    index++;
                    temps.add(templates.getString("name"));
                }
                list_tp.print();
                System.out.print(" Select Template > ");

                String temp = temps.get(input.nextInt()-1);
                input.nextLine();

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

                User.addDev(name,version,insPath,reference,details,temp);
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
                    list_tp.addRow(String.valueOf(index) + ". " + ls(templates.getString("name"),15,0),ls(templates.getString("details"),50,0));
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
