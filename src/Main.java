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
    public static User User;
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

        while(User.auth == 0){
            user_choice = displayLoginMenu();
            input.nextLine();
            switch (user_choice) {
                case 0:
//                    System.out.println("Bye bye");
                    System.exit(0);
                case 1:
                    Cprint.b("\n # Login");
                    Login();
                    break;
                case 2:
                    Cprint.b("\n # Register");
                    Register();
                    break;
                default:
                    Cprint.e(" You've entered a wrong number");
                    break;
            }
        }
        user_choice = -1;
        //Normal User
        if(User.auth == 1){
            while(user_choice != 0){
                user_choice = displayMenu();
                input.nextLine();
                switch (user_choice) {
                    case 0:
                        continue;
                    case 1:
                        Cprint.b("\n # User Information");
                        User.printInfo();
                        break;
                    case 2:
                        Cprint.b("\n # Change Password");
                        User.changePassword();
                        break;
                    case 3:
                        Cprint.b("\n # Your Saved Tools");
                        ManageTools.ShowTools();
                        break;
                    case 4: // Search/Add Installed Tools
                        Cprint.b("\n # [Add] Tools From Local Tools");
                        ManageTools.SearchAdd();
                        break;
                    case 5:
                        Cprint.b("\n # [Add] Tools From Template");
                        addFromTemplate();
                        break;
                    case 6:
                        Cprint.b("\n # [Add] Tools Manually");
                        addProgram();
                        break;
                    case 7:
                        Cprint.b("\n # [Restore] your Tools");
                        ManageTools.Restore();
                        break;
                    case 8:
                        Cprint.e("\n # I sure hope you know what you are doing.");
                        deleteProgram();
                        break;
                    case 9: // Delete Record
                        Cprint.e("\n # I sure hope you know what you are doing.");
                        if(User.Delete()) System.exit(0);
                        else break;
                    default:
                        Cprint.e(" You've entered a wrong number");
                        break;
                }
            }
        }
        //Admin User
        else if(User.auth == 2){
            while(user_choice != 0){
                user_choice = displayAdminMenu();
                input.nextLine();
                switch (user_choice) {
                    case 0:
                        continue;
                    case 1:
                        Cprint.b("\n # User Information");
                        User.printInfo();
                        break;
                    case 2:
                        Cprint.b("\n # View Template - Select Template for Details");
                        Admin.checkProgram();
                        break;
                    case 3:
                        printToolOption();
                        break;
                    case 4:
                        Cprint.b("\n # Add Tool - Enter Information");
                        Admin.addProgram();
                        break;
                    case 5:
                        Cprint.b("\n # Add Tool to Template - Select Template");
                        Admin.addProgramToTemplate();
                        break;
                    case 6:
                        Cprint.b("\n # Add Template - Enter Information");
                        User.createTemplate();
                        break;
                    case 7:
                        Cprint.b("\n # Delete Tool - Select Template");
                        Admin.deleteProgram();
                        break;
                    case 8:
                        Cprint.b("\n # Delete Template - Select Template");
                        Admin.deleteTemplate();
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

    private static void printToolOption(){
        Cprint.b("Search Tools - Select Option");

        TableList list_tp = new TableList(1, "Option").withUnicode(true);
        list_tp.addRow(ls("1. All",20,0));
        list_tp.addRow(ls("2. Default",20,0));
        list_tp.addRow(ls("3. Name",20,0));
        list_tp.addRow(ls("4. ID",20,0)); 
        list_tp.print();      
        System.out.print(" Select Option > ");

        String[] options = {"All","Admin","Name","Uid"};
        int index = input.nextInt()-1;
        Admin.searchTools(options[index]);
        input.nextLine();
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
//            auth = user.Login(uid,pwd);
        }
        while(User.auth == 0);
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
        User.Register(uid, pw, s, name);
    }

    private static void addProgram(){
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

    private static void addFromTemplate(){
        try{
        ResultSet templates = User.getAdminTemplates();
        ArrayList<String> temps = new ArrayList<String>();
        ArrayList<String> programs = new ArrayList<String>();
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
                 programs.add(rs.getString("name"));
                 index++;
            }
            list_tp.print();
            System.out.print(" Select Program > ");
            String program = programs.get(input.nextInt()-1);
            input.nextLine();
            if(User.addProgramToTemplate(program,temp)) Cprint.w(program + " added to your saved tools.");
        } else{
            Cprint.e(" There is no template available :(");
        }
        } catch(Exception e){
        }
    }

    private static void deleteProgram(){
         try{
            ResultSet rs = User.getPrograms_withid();
            ArrayList<String> programs = new ArrayList<String>();
            TableList list_tp = new TableList(2, "Name","Description").withUnicode(true);
            int index = 1;

            while(rs.next()){
                String details = rs.getString("details");
                if(details == null) details = "";
                list_tp.addRow(String.valueOf(index) + ". " + ls(rs.getString("name"),15,0),ls(details,50,0));
                index++;
                programs.add(rs.getString("name"));
            }
            list_tp.print();
            System.out.print(" Select Program > "); 
            String program = programs.get(input.nextInt()-1);
            input.nextLine();

            if(User.deleteProgram(program)) Cprint.w(program + " deleted from your saved tools.");

        } catch(Exception e){
        }
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
        mnu_main.addRow(ls("1. User Information",30,0),ls("3. Show Saved Tools",30,0));
        mnu_main.addRow(ls("2. Change Password",30,0), ls("4. Add Tools From Local Tools",30,0));
        mnu_main.addRow(ls("",30,0),ls("5. Add Tools From Template",30,0));
        mnu_main.addRow(ls("",30,0),ls("6. Add Tools Manually",30,0));
        mnu_main.addRow(ls("",30,0),ls("7. Restore your Tools",30,0));
        mnu_main.addRow(ls("",30,0),ls("8. Delete Tools",30,0));
        mnu_main.addRow(ls("",30,0),ls("",30,0));
        mnu_main.addRow(ls("9. Delete Records",30,0),ls("0. Exit",30,0));
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
        mnu_admin.addRow(ls("3. Search Tools",30,0));
        mnu_admin.addRow(ls("4. Add Tool",30,0));
        mnu_admin.addRow(ls("5. Add Tool to Template",30,0));
        mnu_admin.addRow(ls("6. Add Template",30,0));
        mnu_admin.addRow(ls("7. Delete Tool",30,0));
        mnu_admin.addRow(ls("8. Delete Template",30,0));
        mnu_admin.addRow(ls("0. Exit",30,0));
        mnu_admin.print();
        System.out.print(" Select > ");
        return input.nextInt();
    }

    private static void promptLogin() {
//            System.out.println("\n ---------- Login ----------");
        String uid, pwd;
        System.out.print(" Enter ID: ");
        uid = input.nextLine();
        System.out.print(" Enter Password: ");
        pwd = getPassword();
        User = new User(uid,pwd);
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

    private static String ls(String str, int width, int align){
        if (align == 0) return String.format("%-" + width + "." + width + "s",str);
        else return String.format("%" + width + "." + width + "s",str);
    }
}