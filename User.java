import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class User{
	private Connection conn = null;
	public User() {
		try {
		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://211.249.61.207/DevTools?serverTimezone=UTC", "devtools", "tlftmq1");
		System.out.println("Database Connected");
		} catch(Exception e) {
			System.out.println("Database Connection Error");
			System.out.println(e);
		}
	}
	
	public Boolean Register(String uid, String pwd, String sex, String name){
		PreparedStatement stmt = null;
		try {
		    stmt = conn.prepareStatement(
	    	        "INSERT INTO USER (uid, pwd, sex, name)"
	    	        + " VALUES (?,?,?,?)");
			stmt.setString(1,  uid);
			stmt.setString(2,  pwd);
			if(sex.indexOf("female")>=0) stmt.setString(3, "1");
			else{
				stmt.setString(3, "2");
			}
			stmt.setString(4,name);
			stmt.executeUpdate();
			
			return true;
			
		} catch(Exception e) {
			return false;
		}
	}
}