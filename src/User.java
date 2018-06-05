import java.io.Console;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

				TableList info_user = new TableList(4, "Name","ID","Sex","Type").withUnicode(true);
				while(rs.next()){
					String u_name = rs.getString("name");
					String u_id = rs.getString("uid");
					String u_sex = rs.getInt("sex")==1?"F":"M";
					String u_type = rs.getInt("auth")==1?"General":"Admin";
					info_user.addRow(ls(u_name,20,0),ls(u_id,20,0),ls(u_sex,5,0),ls(u_type,10,0));
				}
				info_user.print();
			}
		} catch(Exception e) {
			System.out.println(e);
			System.out.println(" Failed. Please contact system administrator");
		}

	}
	
	public Boolean createTemplate(){
		String name = "", details = "";
		PreparedStatement stmt = null;
		try {
			System.out.print("Name: ");
			name = input.nextLine();
			System.out.print("Details: ");
			details = input.nextLine();
		    stmt = conn.prepareStatement(
	    	        "INSERT INTO Template (name,createdBy,details)"
	    	        + " VALUES (?,?,?)");
			stmt.setString(1, name);
			stmt.setString(2, "admin");
			stmt.setString(3, details);
			stmt.executeUpdate();

			Cprint.i(name + " template created");
			return true;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Template creation failed. Please contact system administrator\n");
			return false;
		}
	}

	//Admin + 본인 템플릿
	public ArrayList<String> getAdminTemplates(){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<String> output = new ArrayList<String>();
		try {
		    stmt = conn.prepareStatement(
	    	        "SELECT name from Template"
	    	        + " WHERE createdBy = ? ");
			stmt.setString(1, "admin");
			rs = stmt.executeQuery();

			while(rs.next()){
				output.add(rs.getString("name"));
			}
			
			return output;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Failed. Please contact system administrator\n");
			return null;
		}
	}

	//Admin + 본인 템플릿
	public ArrayList<String> getTemplates(String uid){
		PreparedStatement stmt = null;
		ResultSet rs = null;

		ArrayList<String> output = new ArrayList<String>();
		try {
		    stmt = conn.prepareStatement(
	    	        "SELECT name from Template"
	    	        + " WHERE createdBy = ? or createdBy = ? ");
			stmt.setString(1, "admin");
			stmt.setString(2, uid);
			rs = stmt.executeQuery();

			while(rs.next()){
				output.add(rs.getString("name"));
			}
			
			return output;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Failed. Please contact system administrator\n");
			return null;
		}
	}

	public Boolean addDev(String name, String version, String insPath, String reference, String details, String uid, String temp){
		PreparedStatement stmt = null;
		try {
		    stmt = conn.prepareStatement(
	    	        "INSERT INTO Dev (name, version, insPath, reference, details, uid, template)"
	    	        + " VALUES (?,?,?,?,?,?,?)");
			stmt.setString(1, name);
			stmt.setString(2, version);
			stmt.setString(3, insPath);
			stmt.setString(4, reference);
			stmt.setString(5, details);
			stmt.setString(6, uid);
			stmt.setString(7, temp);
			stmt.executeUpdate();

			Cprint.i(" [" + name + "] added to " + temp + " Template successfully");
			return true;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Program adding failed. Please contact system administrator\n");
			return false;
		}
	}

	public ResultSet getPrograms(String template){
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
		    stmt = conn.prepareStatement(
	    	        "SELECT * FROM Dev"
	    	        + " WHERE template = ?");
			stmt.setString(1, template);
			rs = stmt.executeQuery();
			return rs;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Failed. Please contact system administrator\n");
			return null;
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

	private static String ls(String str, int width, int align){
		if (align == 0) return String.format("%-" + width + "." + width + "s",str);
		else return String.format("%" + width + "." + width + "s",str);
	}
	
}