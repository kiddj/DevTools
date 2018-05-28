public class Main{
	public static void main(String args[]){
		User user =  new User();
		if(user.Login("", "")) {
			displayMenu();
		};
	}
	
	public static void displayMenu() {
		System.out.println("1. Display x");
		System.out.println("2. Change Password");
		//etc...
	}
}