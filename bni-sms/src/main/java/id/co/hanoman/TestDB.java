package id.co.hanoman;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDB {
	private static Logger LOG = LoggerFactory.getLogger(TestDB.class);
	
	public static void dump(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		StringWriter sw = new StringWriter();
		PrintWriter out = new PrintWriter(sw);
		for (int row=1; rs.next(); row++) {
			out.println("row "+row+" {");
			for (int i=1, il=md.getColumnCount(); i<=il; i++) {
				out.println("\t"+md.getColumnName(i)+" = ["+rs.getObject(i)+"]");
			}
			out.println("}");
		}
		LOG.info("DATA\n"+sw.toString());
	}
	
	public static void mainx(String[] args) {
		Connection conn = null;
		PreparedStatement ps = null, ps1 = null;
		ResultSet rs = null;
		try {
			LOG.info("connecting to db");
			conn = DriverManager.getConnection("jdbc:mysql://10.211.55.4:3306/bnisms", "seno", "dodol123");
			conn.setAutoCommit(false);
			LOG.info("connected");
			
//			ps = conn.prepareStatement("insert into push_sms(text) values(?)");
//			for (int i=1; i<1000; i++) {
//				ps.setString(1, "DATA NO "+i);
//				ps.executeUpdate();
//			}
//			
//			ps = conn.prepareStatement("select id, text from push_sms");
//			rs = ps.executeQuery();
//			
//			ResultSetMetaData md = rs.getMetaData();
//			for (int i=1, il=md.getColumnCount(); i<=il; i++) {
//				LOG.info("COLUMN "+i+" "+md.getColumnName(i)+" "+md.getColumnTypeName(i));
//			}
//			
//			LOG.info("BEGIN");
//			while (rs.next()) {
//				LOG.info("DATA "+rs.getLong(1)+" ["+rs.getString(2)+"]");
//			}
//			LOG.info("END");
			
			
			ps = conn.prepareStatement("select id from push_sms where task_status is null order by id asc limit 10 for update");
			rs = ps.executeQuery();
			
			List<Long> list = new LinkedList<Long>();
			while (rs.next()) list.add(rs.getLong(1));
			
			LOG.info("DATA "+list);
			
			ps1 = conn.prepareStatement("update push_sms set task_status = ?, task_timestamp = ? where id = ?");
			for (long l : list) {
				ps1.setString(1, "inprogress");
				ps1.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				ps1.setLong(3, l);
				ps1.executeUpdate();
			}
			
			conn.commit();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception e) {}
			}
			if (ps1 != null) {
				try {
					ps1.close();
				} catch (Exception e) {}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {}
			}
		}
	}
}
