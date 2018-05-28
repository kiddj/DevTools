import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class User{

	private Connection conn = null;
	private MessageDigest md = null;
	
	public User() {
		try {
		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://211.249.61.207/DevTools?serverTimezone=UTC", "devtools", "tlftmq1");
		System.out.println("Database Connected");
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
			md.update(pwd.getBytes());
			pwd = bytesToHex(md.digest());
			stmt.setString(2,  pwd);
			rs = stmt.executeQuery();

			if(rs.next()){
				System.out.println("Login Success");
				return true;
		  } else{
		  	System.out.println("Please check your ID and Password");
		  	return false;
		  }
		} catch(Exception e) {
			System.out.println(e);
			System.out.println("Error occured");
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
}