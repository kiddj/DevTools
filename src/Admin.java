import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

public class Admin {

    private static Scanner input = new Scanner(System.in);

    public static void addProgram() {
        ArrayList<String> templates = User.getAdminTemplates();
        if(templates.size() > 0){
            TableList list_tp = new TableList(2, "Name","Description").withUnicode(true);

            int index = 1;
            for(String template : templates){
                list_tp.addRow(String.valueOf(index) + ". "  + ls(template,15,0),ls("",50,0));
                index++;
            }
            list_tp.print();
            System.out.print(" Select Template > ");

            String temp = templates.get(input.nextInt()-1);
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
    }

    public static void checkProgram(){
        ArrayList<String> templates = User.getAdminTemplates();
        if(templates.size() > 0){
            TableList list_tp = new TableList(2, "Name","Description").withUnicode(true);

            int index = 1;
            for(String template : templates){
                list_tp.addRow(String.valueOf(index) + ". "  + ls(template,15,0),ls("",50,0));
                index++;
            }
            list_tp.print();
            System.out.print(" Select Template > ");

            String temp = templates.get(input.nextInt()-1);
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
    }

    private static String ls(String str, int width, int align){
        if (align == 0) return String.format("%-" + width + "." + width + "s",str);
        else return String.format("%" + width + "." + width + "s",str);
    }

}