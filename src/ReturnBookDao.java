import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReturnBookDao {
	public static int delete(String bookcallno,int studentid){
		int status=0;
		try{
			Connection con=DB.getConnection();
			
			status=updatebook(bookcallno);//updating quantity and issue
			
			if(status>0){
			PreparedStatement ps=con.prepareStatement("delete from issuebooks where bookcallno=? and studentid=?");
			ps.setString(1,bookcallno);
			ps.setInt(2,studentid);
			status=ps.executeUpdate();
			}
			
			con.close();
		}catch(Exception e){System.out.println(e);}
		return status;
	}
	public static int updatebook(String bookcallno){
		int status=0;
		int quantity=0,issued=0;
		try{
			Connection con=DB.getConnection();
			
			PreparedStatement ps=con.prepareStatement("select quantity,issued from books where callno=?");
			ps.setString(1,bookcallno);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				quantity=rs.getInt("quantity");
				issued=rs.getInt("issued");
			}
			
			if(issued>0){
			PreparedStatement ps2=con.prepareStatement("update books set quantity=?,issued=? where callno=?");
			ps2.setInt(1,quantity+1);
			ps2.setInt(2,issued-1);
			ps2.setString(3,bookcallno);
			
			status=ps2.executeUpdate();
			}
			con.close();
		}catch(Exception e){System.out.println(e);}
		return status;
	}
	public static long findValue(String bookcallno,int studentid) {
		Timestamp t=null;
		String days="";
		long diff=0,diffm=0;
		
		try {
			Connection con=DB.getConnection();
			PreparedStatement ps=con.prepareStatement("select issueddate from issuebooks where bookcallno=? and studentid=?");
			ps.setString(1, bookcallno);
			ps.setInt(2, studentid);
			
			ResultSet r=ps.executeQuery();
			
			t=r.getTimestamp("issueddate");
			
			
			Date date=new Date(t.getTime());
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date cDate = new Date();
			cDate=formatter.parse(formatter.format(cDate));
			
			if(cDate.after(date)) {
				
				diff=Math.abs(cDate.getTime()-date.getTime());
				diffm=diff / (24 * 60 * 60 * 1000);
			}
			
		}
		catch(Exception e) {
			
		}
		if(diffm>2) {
		diffm=diffm*10;
		return diffm;
		}
		else {
			return 0;
		}
		
		
	}
}
