import java.io.Console;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.io.Console;

public class User{
	private Connection conn = null;
	private MessageDigest md = null;
	private static Scanner input = new Scanner(System.in);
	
	public User() {
		try {
		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://211.249.61.207/DevTools?serverTimezone=UTC&useSSL=false", "devtools", "tlftmq1");
		//System.out.println("Database Connected");
		md = MessageDigest.getInstance("SHA-256");
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Database Connection Error");
		}
	}

	public Boolean Delete(String uid){
		//Confirm
		Cprint.e(" Are you sure you want to delete your account? (y,n): ","");
		String cfm = input.nextLine().toLowerCase();

		if(cfm.equals("y")){
			PreparedStatement stmt = null;
			try {
			    stmt = conn.prepareStatement(
		    	        "DELETE FROM User"
		    	        + " WHERE uid = ?");
				stmt.setString(1,  uid);
				stmt.executeUpdate();
				Cprint.w(" User information successfully deleted. Thank you for using our service.");
				return true;
				
			} catch(Exception e) {
                Cprint.e(" Error occurs: " + e);
				Cprint.e("\n Failed. Please contact system administrator\n");
				return false;	
			}
		} else{
			return false;
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

			if(sex.toLowerCase().indexOf("f")>=0) stmt.setString(3, "1");
			else{
				stmt.setString(3, "2");
			}
			stmt.setString(4,name);
			stmt.executeUpdate();
			Cprint.i(" You've successfully registered to DevTools\n");
			return true;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Registration Failed. Please contact system administrator\n");
			return false;
		}
	}

	public int Login(String uid, String pwd){
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
				Cprint.i("\n Welcome "+rs.getString("name")+"!\n");
				return rs.getInt("auth");
		  } else{
			Cprint.e(" Login failed: Please check your ID and Password");
		  	return 0;
		  }
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			return 0;
		}
	}

	public Boolean changePassword(String uid) {
		PreparedStatement stmt = null;

//		System.out.println(" ---------- Change Password ----------");
		System.out.print(" New Password : ");
		String newPwd = getPassword();
		System.out.print(" Confirm New Password : ");
		String confirmPwd = getPassword();
			
		if(!newPwd.equals(confirmPwd)) {
			Cprint.e(" Error: Passwords you have entered do not match\n");
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

			Cprint.i(" Password successfully changed\n");
			return true;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			return false;
		}
	}

	public void printInfo(String uid){
		PreparedStatement stmt = null;
		String sex = "", level = "";
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(
	    	        "SELECT * from User "
	    	        + "WHERE uid = ?");
			stmt.setString(1,  uid);
			rs = stmt.executeQuery();

			if(rs.next()){
				String whereClause = "";
				if(rs.getInt("auth") == 1){
			  	stmt = conn.prepareStatement(
		    	        "SELECT uid,name,sex,auth from User "
		    	        + "WHERE uid = ?");
					stmt.setString(1,  uid);
				} else{
					stmt = conn.prepareStatement("SELECT uid,name,sex,auth from User");
				}
				rs = stmt.executeQuery();
				
				while(rs.next()){
					Cprint.b(" Name: " + rs.getString("name"));
					System.out.println(" Id:   " + rs.getString("uid"));
					if(rs.getInt("sex") == 1) sex = "Female";
					else sex = "Male";
					System.out.println(" Sex:  " + sex);
					if(rs.getInt("auth") == 1) level = "General";
					else level = "Admin";
					System.out.println(" Type: " + level);
					System.out.println("");
				}
			}
		} catch(Exception e) {
			System.out.println(e);
			System.out.println(" Failed. Please contact system administrator");
		}

	}
	
	public Boolean createTemplate(String uid){
		String name = "";
		PreparedStatement stmt = null;
		try {

			System.out.print("Please enter the name of the tool template: ");
			name = input.nextLine();
		    stmt = conn.prepareStatement(
	    	        "INSERT INTO Template (name,createdBy)"
	    	        + " VALUES (?,?)");
			stmt.setString(1, name);
			stmt.setString(2, uid);
			stmt.executeUpdate();

			Cprint.i(name + " template created");
			return true;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Template creation failed. Please contact system administrator\n");
			return false;
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
	
}