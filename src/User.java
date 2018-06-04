import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class User{
	private Connection conn = null;
	private MessageDigest md = null;
	private Scanner input = new Scanner(System.in);
	
	public User() {
		try {
		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://211.249.61.207/DevTools?serverTimezone=UTC&useSSL=false", "devtools", "tlftmq1");
		//System.out.println("Database Connected");
		md = MessageDigest.getInstance("SHA-256");
		} catch(Exception e) {
			System.out.println("Database Connection Error");
			System.out.println(e);
		}
	}
	
	public Boolean Register(String uid, String pwd, String sex, String name){
		PreparedStatement stmt = null;
		try {
		    stmt = conn.prepareStatement(
	    	        "INSERT INTO User (uid, pwd, sex, name)"
	    	        + " VALUES (?,?,?,?)");
			stmt.setString(1,  uid);
			//apply SHA-256
			md.update(pwd.getBytes());
			pwd = bytesToHex(md.digest());
			stmt.setString(2,  pwd);

			if(sex.indexOf("female")>=0) stmt.setString(3, "1");
			else{
				stmt.setString(3, "2");
			}
			stmt.setString(4,name);
			stmt.executeUpdate();
			System.out.println("You've successfully registered to DevTools");
			return true;
			
		} catch(Exception e) {
			System.out.println(e);
			System.out.println("Registration Failed. Please contact system administrator");
			return false;
		}
	}

	public Boolean Login(String uid, String pwd){
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
		    stmt = conn.prepareStatement(
	    	        "SELECT * from User"
	    	        + " WHERE uid=? and pwd=?");
			stmt.setString(1,  uid);
			//apply SHA-256
			md.update(pwd.getBytes()) ;
			pwd = bytesToHex(md.digest());
			stmt.setString(2,  pwd);
			rs = stmt.executeQuery();

			if(rs.next()){
				System.out.println("\n*****************************************************");
				System.out.println("Welcome "+rs.getString("name")+"!\n");
				return true;
		  } else{
		  	System.out.println("Login failed: Please check your ID and Password");
		  	return false;
		  }
		} catch(Exception e) {
			System.out.println(e);
			System.out.println("Error occured");
			return false;
		}
	}
	
	public Boolean changePassword(String uid, String pwd) {
		PreparedStatement stmt = null;
		if(Login(uid, pwd)) {
			System.out.print("New Password :: ");
			String newPwd = input.nextLine();
			System.out.print("Confirm New Password :: ");
			String confirmPwd = input.nextLine();
			
			if(!newPwd.equals(confirmPwd)) {
				System.out.println("Passwords you have entered do not match");
				return false;
			}
			
			//apply SHA-256
			md.update(newPwd.getBytes());
			newPwd = bytesToHex(md.digest());
			
			try {
			    stmt = conn.prepareStatement(
		    	        "UPDATE User SET pwd=?"
		    	        + " WHERE uid=?");
				
				stmt.setString(1, newPwd);
				stmt.setString(2, uid);
				stmt.executeUpdate();

				System.out.println("Password successfully changed");
				return true;
			} catch(Exception e) {
				System.out.println(e);
				System.out.println("Error occured");
				return false;
			}
		}else {
			return false;
		}
	}

	public void printInfo(String uid){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			System.out.println("\n------------------------------------");

		  stmt = conn.prepareStatement(
	    	        "SELECT uid,name,sex from User "
	    	        + "WHERE uid = ?");
			stmt.setString(1,  uid);
			rs = stmt.executeQuery();
			
			if(rs.next()){
				System.out.println("Name: " + rs.getString("name") + "\n");
				System.out.println("Id: " + rs.getString("uid") + "\n");
				String sex = "";
				if(rs.getInt("sex") == 1) sex = "Female";
				else sex = "Male";
				System.out.println("Sex: " + sex);
				System.out.println("\n------------------------------------");
				System.out.println("");
			}
		} catch(Exception e) {
			System.out.println(e);
			System.out.println("Failed. Please contact system administrator");
		}
	}
	
	private static String bytesToHex(byte[] hash) {
	    StringBuffer hexString = new StringBuffer();
	    for (int i = 0; i < hash.length; i++) {
	    String hex = Integer.toHexString(0xff & hash[i]);
	    if(hex.length() == 1) hexString.append('0');
	        hexString.append(hex);
	    }
	    return hexString.toString();
	}
}