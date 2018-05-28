import java.util.Scanner;

public class Main{
	private static Scanner input = new Scanner(System.in);
	private static User user =  new User();
	private static String uid, pwd;
	
	public static void main(String args[]){
		do {
			promptLogin();
		}
		while(!user.Login(uid, pwd));
		
		
		displayMenu(); 
	}
	
	public static void displayMenu() {
		System.out.println("1. Display x");
		System.out.println("2. Change Password");
		//etc...
	}
	
	public static void promptLogin() {
		System.out.print("ID : ");
		uid = input.nextLine();
		System.out.print("Password : ");
		pwd = input.nextLine();
	}
}