import java.io.IOException;
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
        Sysinfo systest = new Sysinfo();
        systest.readInfo();

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