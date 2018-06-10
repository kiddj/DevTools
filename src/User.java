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
	private static Connection conn = null;
	private static MessageDigest md = null;
	private static Scanner input = new Scanner(System.in);

	// User info
    public static String uid, pwd;
    public static int auth = 0;

    public User(){
		connectDB();
	}

	private static void connectDB(){
		try {
			if(conn!=null) return;
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://211.249.61.207/DevTools?serverTimezone=UTC&useSSL=false", "devtools", "tlftmq1");
			//System.out.println("Database Connected");
			md = MessageDigest.getInstance("SHA-256");
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Database Connection Error");
		}
	}

	public static Boolean Delete(){
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
	
	public static Boolean Register(String uid, String pwd, String sex, String name){
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

	public static int Login(String user_uid, String user_pw){
		uid = user_uid;
		pwd = user_pw;
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
				auth = rs.getInt("auth");
				return 0;
		  } else{
			Cprint.e(" Login failed: Please check your ID and Password");
		  	return 0;
		  }
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			return 0;
		}
	}

	public static Boolean changePassword() {
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

	public static void printInfo(){
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
	
	public static Boolean createTemplate(){
		String name = "", details = "";
		PreparedStatement stmt = null;
		try {
			System.out.print(" Name: ");
			name = input.nextLine();
			System.out.print(" Details: ");
			details = input.nextLine();
		    stmt = conn.prepareStatement(
	    	        "INSERT INTO Template (name,createdBy,details)"
	    	        + " VALUES (?,?,?)");
			stmt.setString(1, name);
			stmt.setString(2, "admin");
			stmt.setString(3, details);
			stmt.executeUpdate();

			Cprint.i(" " + name + " template created");
			return true;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Template creation failed. Please contact system administrator\n");
			return false;
		}
	}

	//Admin + 본인 템플릿
	public static ResultSet getAdminTemplates(){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
		    stmt = conn.prepareStatement(
	    	        "SELECT * from Template"
	    	        + " WHERE createdBy = ? ");
			stmt.setString(1, "admin");
			rs = stmt.executeQuery();

			return rs;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Failed. Please contact system administrator\n");
			return null;
		}
	}
	public static ResultSet getTools(String type, String search){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String op = "=";
		if(type.equals("name")){
			op = "LIKE";
			search = "%"+search+"%";
		} 

		try {
		    stmt = conn.prepareStatement(
	    	        "SELECT DISTINCT Dev.name, Dev.version, Dev.details from Dev "
	    	        + "WHERE " + type + " " + op + " ?");
			stmt.setString(1, search);
			//stmt.setString(2, search);
			rs = stmt.executeQuery();

			return rs;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Failed. Please contact system administrator\n");
			return null;
		}
	}
	//Admin + 본인 템플릿
	public static ResultSet getTemplates(){
		PreparedStatement stmt = null;
		ResultSet rs = null;

		ArrayList<String> output = new ArrayList<String>();
		try {
		    stmt = conn.prepareStatement(
	    	        "SELECT * from Template"
	    	        + " WHERE createdBy = ? or createdBy = ? ");
			stmt.setString(1, "admin");
			stmt.setString(2, uid);
			rs = stmt.executeQuery();

			while(rs.next()){
				output.add(rs.getString("name"));
			}
			
			return rs;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Failed. Please contact system administrator\n");
			return null;
		}
	}

	public static Boolean addDev(String name, String version, String insPath, String reference, String details, String temp){
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
			if (temp!= null) Cprint.i("\n [" + name + " " + version + "] added to " + temp + " Template successfully");
			else Cprint.i("\n [" + name + " " + version + "] saved successfully");
			return true;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Program adding failed. Please contact system administrator\n");
			return false;
		}
	}

	public static Boolean addProgramToTemplate(SWinfo program, String template){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT * from Dev WHERE name = ? and version = ?");
			stmt.setString(1, program.name);
			stmt.setString(2, program.version);
			rs = stmt.executeQuery();

		    stmt = conn.prepareStatement(
	    	        "INSERT INTO Dev (name, version, insPath, reference, details, uid, template)"
	    	        + " VALUES (?,?,?,?,?,?,?)");
		    if(rs.next()){
			stmt.setString(1, rs.getString("name"));
			stmt.setString(2, rs.getString("version"));
			stmt.setString(3, rs.getString("insPath"));
			stmt.setString(4, rs.getString("reference"));
			stmt.setString(5, rs.getString("details"));
			stmt.setString(6, uid);
			stmt.setString(7, template);
			stmt.executeUpdate();
			return true;
			}
			else return false;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Program adding failed. Please contact system administrator\n");
			return false;
		}
	}

	public static ResultSet getPrograms(String template){
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			if (template != null) {	// Dev in template
				stmt = conn.prepareStatement(
						"SELECT * FROM Dev"
								+ " WHERE template = ?");
				stmt.setString(1, template);
			} else {	// All Dev created by admin (independent with template -> distinct)
				stmt = conn.prepareStatement(
						"SELECT DISTINCT Dev.name, Dev.version, Dev.insPath, Dev.details, Dev.reference FROM Dev WHERE uid = 'admin'");
			}
			rs = stmt.executeQuery();
			return rs;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Failed. Please contact system administrator\n");
			return null;
		}
	}

	public static ResultSet getAllPrograms(){
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareStatement(
						"SELECT DISTINCT Dev.name, Dev.version, Dev.insPath, Dev.details, Dev.reference FROM Dev");
			rs = stmt.executeQuery();
			return rs;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Failed. Please contact system administrator\n");
			return null;
		}
	}

	// No template (For general User)
	public static ResultSet getPrograms_withid(){
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareStatement(
			"SELECT DISTINCT Dev.name, Dev.version, Dev.insPath, Dev.details, Dev.reference FROM Dev WHERE uid = ?");
			stmt.setString(1, uid);
			rs = stmt.executeQuery();
			return rs;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Failed. Please contact system administrator\n");
			return null;
		}
	}

	public static Boolean deleteTemplate(String template){
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(
					"DELETE FROM Template"
							+ " WHERE name = ?");
			stmt.setString(1, template);
			stmt.executeUpdate();

			return true;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Failed. Please contact system administrator\n");
			return false;
		}
	}

	public static Boolean deleteProgram(SWinfo sw_del, String tmp){
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(
					"DELETE FROM Dev"
							+ " WHERE name = ? and version = ? and uid = ? and template "
							+ (tmp==null?"is null":("= \'"+ tmp + "\'" )));
			stmt.setString(1, sw_del.name);
			stmt.setString(2, sw_del.version);
			stmt.setString(3, uid);
			stmt.executeUpdate();
			return true;
		} catch(Exception e) {
			Cprint.e(" Error occurs: " + e);
			Cprint.e(" Failed. Please contact system administrator\n");
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

	private static String ls(String str, int width, int align){
		if (align == 0) return String.format("%-" + width + "." + width + "s",str);
		else return String.format("%" + width + "." + width + "s",str);
	}
	
}