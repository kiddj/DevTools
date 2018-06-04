import java.io.Console;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

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
        displayLogo();

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
                    System.out.println(" You've entered a wrong number");
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
                    case 4:
                        break;
                    case 5: // Search/Add Installed Tools
                        ManageTools.SearchAdd();
                        break;
                    case 6:
                        break;
                    case 7:
                        if(user.Delete(uid)) System.exit(0);
                        else break;
                    default:
                        System.out.println(" You've entered a wrong number");
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
                        break;
                    default:
                        System.out.println(" You've entered a wrong number");
                        break;
                }
            }
        }

        //Terminate => Maybe backup automatically...
        System.out.println("Thank you");
        System.exit(0);
    }

    private static void displayLogo() {
        System.out.println("  _____ _____ _____ _____ _____ _____ __    _____ ");
        System.out.println(" |     |   __|  |  |_   _|     |     |  |  |   __|");
        System.out.println(" |  |  |   __|  |  | | | |  |  |  |  |  |__|__   |  {version : " + str_ver + "}");
        System.out.println(" |____/|_____|____/  |_| |_____|_____|_____|_____|  github.com/kiddj/DevTools\n");
    }

    private static void Login(){
        // Login
        do {
            promptLogin();
            auth = user.Login(uid,pwd);
        }
        while(auth == 0);
    }

    private static void Register(){
        String name, uid, pw, cpw, s;
        System.out.print(" Name : ");
        name = input.nextLine();
        System.out.print(" ID : ");
        uid = input.nextLine();
        pw = getPassword();
        System.out.print(" Confirm ");
        cpw = getPassword();
        System.out.print(" Sex (M,F) : ");
        s = input.nextLine();

        if(pw.equals(cpw)) user.Register(uid, pw, s, name);
        else System.out.println(" The passwords you've entered do not match\n");
    }

    private static int displayLoginMenu() {
        System.out.println("┌-------------------------------┐");
        System.out.println("│ 1. Login                      │");
        System.out.println("│ 2. Register                   │");
        System.out.println("│ 0. Exit                       │");
        System.out.println("└-------------------------------┘");
        System.out.print(" Select: ");
        return input.nextInt();
    }

    private static int displayMenu() {
        System.out.println(" DevTools " + str_ver);
        System.out.println("┌--------------------------------┬---------------------------------┐");
        System.out.println("│            Your Info           │     Manage Development Tools    │");
        System.out.println("├--------------------------------┼---------------------------------┤");
        System.out.println("│ 1. Display User Information    │ 3. Show Saved Tools             │");
        System.out.println("│ 2. Change Password             │ 4. Add Tools Manually           │");
        System.out.println("│                                │ 5. Search/Add Installed Tools   │");
        System.out.println("│                                │ 6. Restore your Tools           │");
        System.out.println("├--------------------------------┼---------------------------------┤");
        System.out.println("│       ## Warning Zone ##       │ 7. Delete Record        0. Exit │");
        System.out.println("└--------------------------------┴---------------------------------┘");
        System.out.print(" Select: ");
        return input.nextInt();
    }

    private static int displayAdminMenu() {
        System.out.println("------------------------------------");
        System.out.println("1. Display All User Information");
        System.out.println("2. Add Template");
        System.out.println("0. Exit");
        System.out.println("------------------------------------\n");
        return input.nextInt();
    }

    private static void promptLogin() {
        System.out.print(" Enter ID: ");
        uid = input.nextLine();
        pwd = getPassword();
    }

    private static String getPassword() {
        Console console = System.console();

        if (console == null) {
            System.out.println(" ! Fail to Mask your Password :( - Couldn't get Console instance");
//	        System.exit(0);
            System.out.print(" Enter Password: ");
            String passwordString = input.nextLine();
            return passwordString;
        }

        char[] passwordArray = console.readPassword(" Enter Password: ");
        return new String(passwordArray);
    }

}