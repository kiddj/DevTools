import java.io.Console;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;
import java.util.ArrayList;
import java.sql.ResultSet;

//TODO
//Devtools Sorting

public class Main{
    private static Scanner input = new Scanner(System.in);
    private static User user =  new User();
    private static String uid, pwd;
    private static int auth = 0;
    private static String str_ver = "0.1.0";

    //Test all encoding type
    private static void encoding_test(String str){
        String originalStr = str;
        String [] charSet = {"utf-8","euc-kr","ksc5601","iso-8859-1","x-windows-949"};

        for (int i=0; i<charSet.length; i++) {
            for (int j=0; j<charSet.length; j++) {
                try {
                    System.out.println("[" + charSet[i] +"," + charSet[j] +"] = " + new String(originalStr.getBytes(charSet[i]), charSet[j]));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String args[]) throws Exception{
        AnsiConsole.systemInstall( );
        System.out.print(ansi().eraseScreen());

        // Select Menu
        int user_choice = -1;

        while(auth == 0){
            user_choice = displayLoginMenu();
            input.nextLine();
            switch (user_choice) {
                case 0:
//                    System.out.println("Bye bye");
                    System.exit(0);
                case 1:
                    Login();
                    break;
                case 2:
                    Register();
                    break;
                default:
                    Cprint.e(" You've entered a wrong number");
                    break;
            }
        }
        user_choice = -1;
        //Normal User
        if(auth == 1){
            while(user_choice != 0){
                user_choice = displayMenu();
                input.nextLine();
                switch (user_choice) {
                    case 0:
                        continue;
                    case 1:
                        user.printInfo(uid);
                        break;
                    case 2:
                        user.changePassword(uid);
                        break;
                    case 3:
                        break;
                    case 4: // Search/Add Installed Tools
                        ManageTools.SearchAdd();
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7: // Delete Record
                        if(user.Delete(uid)) System.exit(0);
                        else break;
                    default:
                        Cprint.e(" You've entered a wrong number");
                        break;
                }
            }
        }
        //Admin User
        else if(auth == 2){
            while(user_choice != 0){
                user_choice = displayAdminMenu();
                input.nextLine();
                switch (user_choice) {
                    case 0:
                        continue;
                    case 1:
                        user.printInfo(uid);
                        break;
                    case 2:
                        checkProgram();
                        break;
                    case 3:
                        addProgram();
                        break;
                    case 4:
                        user.createTemplate();
                        break;
                    default:
                        Cprint.e(" You've entered a wrong number");
                        break;
                }
            }
        }

        //Terminate => Maybe backup automatically...
        System.exit(0);
    }

    private static void displayLogo() {
//        System.out.print(ansi().eraseScreen());
        Cprint.w("  _____ _____ _____ _____ _____ _____ __    _____ ");
        Cprint.w(" |     |   __|  |  |_   _|     |     |  |  |   __|");
        Cprint.w(" |  |  |   __|  |  | | | |  |  |  |  |  |__|__   |  {version : " + str_ver + "}");
        Cprint.w(" |____/|_____|____/  |_| |_____|_____|_____|_____|  github.com/kiddj/DevTools\n");
    }

    private static void Login() {
        // Login
        do {
            promptLogin();
            auth = user.Login(uid,pwd);
        }
        while(auth == 0);
    }

    private static void Register() {
        int pw_check = 0;
        String name, uid, pw = null, cpw, s;
//        System.out.println("\n ---------- Register ----------");
        System.out.print(" Name : ");
        name = input.nextLine();
        System.out.print(" ID : ");
        uid = input.nextLine();
        while(pw_check != 1){
            System.out.print(" Password: ");
            pw = getPassword();
            System.out.print(" Confirm Password: ");
            cpw = getPassword();
            if(pw.equals(cpw)) pw_check = 1;
            else Cprint.e(" The passwords you've entered do not match\n");
        }
        System.out.print(" Sex (M,F) : ");
        s = input.nextLine();
        user.Register(uid, pw, s, name);
    }

    private static int displayLoginMenu() {
        displayLogo();
        TableList mnu_login = new TableList(1, "Login to DevTools").withUnicode(true);
        mnu_login.addRow(ls("1. Login",30,0));
        mnu_login.addRow(ls("2. Register",30,0));
        mnu_login.addRow(ls("0. Exit",30,0));
        mnu_login.print();
        System.out.print(" Select > ");
        return input.nextInt();
    }

    private static int displayMenu() {
//        System.out.println(" DevTools " + str_ver);
        displayLogo();
        TableList mnu_main = new TableList(2, "Manage Info","Manage Development Tools").withUnicode(true);
        mnu_main.addRow(ls("1. User Information",30,0),ls("3. Show my Tools",30,0));
        mnu_main.addRow(ls("2. Change Password",30,0), ls("4. Search/Add Installed Tools",30,0));
        mnu_main.addRow(ls("",30,0),ls("5. Add Tools Manually",30,0));
        mnu_main.addRow(ls("",30,0),ls("6. Restore your Tools",30,0));
        mnu_main.addRow(ls("",30,0),ls("",30,0));
        mnu_main.addRow(ls("7. Delete Records",30,0),ls("0. Exit",30,0));
        mnu_main.print();
//        System.out.println("┌--------------------------------┬---------------------------------┐");
//        System.out.println("│            Your Info           │     Manage Development Tools    │");
//        System.out.println("├--------------------------------┼---------------------------------┤");
//        System.out.println("│ 1. Display User Information    │ 3. Show Saved Tools             │");
//        System.out.println("│ 2. Change Password             │ 4. Add Tools Manually           │");
//        System.out.println("│                                │ 5. Search/Add Installed Tools   │");
//        System.out.println("│                                │ 6. Restore your Tools           │");
//        System.out.println("├--------------------------------┼---------------------------------┤");
//        System.out.println("│       ## Warning Zone ##       │ 7. Delete Record        0. Exit │");
//        System.out.println("└--------------------------------┴---------------------------------┘");
        System.out.print(" Select > ");
        return input.nextInt();
    }

    private static int displayAdminMenu() {
        displayLogo();
        TableList mnu_admin = new TableList(1, "Admin").withUnicode(true);
        mnu_admin.addRow(ls("1. User Information",30,0));
        mnu_admin.addRow(ls("2. View Template",30,0));
        mnu_admin.addRow(ls("3. Add Tools to Template",30,0));
        mnu_admin.addRow(ls("4. Add Template",30,0));
        mnu_admin.addRow(ls("0. Exit",30,0));
        mnu_admin.print();
        System.out.print(" Select > ");
        return input.nextInt();
    }

    private static void promptLogin() {
//            System.out.println("\n ---------- Login ----------");
        System.out.print(" Enter ID: ");
        uid = input.nextLine();
        System.out.print(" Enter Password: ");
        pwd = getPassword();
    }

    private static String getPassword() {
        Console console = System.console();

        if (console == null) {
            Cprint.e(" Failed to Mask your Password :( - Couldn't get Console instance");
//	        System.exit(0);
            String passwordString = input.nextLine();
            return passwordString;
        }

        char[] passwordArray = console.readPassword("");
        return new String(passwordArray);
    }

    private static void addProgram() {
        ArrayList<String> templates = user.getAdminTemplates();
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

            user.addDev(name,version,insPath,reference,details,uid,temp);
        } else{
            Cprint.e(" There is no template available :(");
        }
    }

    private static void checkProgram(){
        ArrayList<String> templates = user.getAdminTemplates();
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

            ResultSet rs = user.getPrograms(temp);

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