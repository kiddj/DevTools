import java.io.Console;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Main{
	private static Scanner input = new Scanner(System.in);
	private static User user =  new User();
	private static String uid, pwd;

	//Test all encoding type
	public static void encoding_test(String str){
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
		do {
			promptLogin();
		}
		while(!user.Login(uid, pwd));

		displayMenu();
	}

	public static void displayMenu() {
		System.out.println("1. Display User Information");
		System.out.println("2. Change Password");
		System.out.println("3. Add Development Tools");
		System.out.println("4. Show Added Tools");
		System.out.println("5. Search Tools");
		System.out.println("6. Delete Record");
		switch(input.nextInt()){
			case 1: break;
			case 2: break;
			case 3: break;
			case 4: break;
			case 5: break;
			case 6: break;
			default: System.out.println("You've entered a wrong number");break;
		}
		
	public static void promptLogin() {
		System.out.print("ID : ");
		uid = input.nextLine();
		pwd = getPassword();
	}
	
	public static String getPassword() {        
	    Console console = System.console();
	    if (console == null) {
	        System.out.println("Couldn't get Console instance");
	        System.exit(0);
	    }

	    char passwordArray[] = console.readPassword("Password: ");
	    return new String(passwordArray);
	}
	
}