package id.co.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class InsertDataRTL_SMSN_TEST {

	public static void main(String[] adb) throws SQLException, Exception {
		PreparedStatement preparedStatement;
		try {
			preparedStatement = InsertDataRTL_SMSN_TEST
					.getKoneksi()
					.prepareStatement(
							"insert into ugw.rtl_smsn_test (ACCT_NO, JRNL_NO, TRAN_DATE, TRAN_TIME, NO_HP, CIF, TRAN_TYPE, TRAN_CODE, AMOUNT, CURR_BAL, ACCT_FROM_TO, "
									+ "NARRATIVE_1, NARRATIVE_2, NARRATIVE_3, NOTIF_TYPE, EXTRACT_FLAG, DELIVERED, FEE_FLAG, DELIVERED_TIME, FEE_TIME, DELIVERED_PHONE, CREATED_DATE, SMSN_ID) "
									+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			for(int i=2; i<=1000; i++) {
				preparedStatement.setString(1, "AAA");
				preparedStatement.setString(2, "BBB");
				preparedStatement.setBigDecimal(3, new BigDecimal("40843"));
				preparedStatement.setString(4, "13162164");
				preparedStatement.setString(5, "0845435"+i);
				preparedStatement.setString(6, null);
				preparedStatement.setString(7, null);
				preparedStatement.setString(8, null);
				preparedStatement.setBigDecimal(9, null);
				preparedStatement.setBigDecimal(10, null);
				preparedStatement.setString(11, null);
				preparedStatement.setString(12, null);
				preparedStatement.setString(13, null);
				preparedStatement.setString(14, "Pesan Narrative 3 "+i);
				preparedStatement.setString(15, null);
				preparedStatement.setString(16, null);
				preparedStatement.setString(17, null);
				preparedStatement.setString(18, null);
				preparedStatement.setDate(19, null);
				preparedStatement.setDate(20, null);
				preparedStatement.setString(21, null);
				preparedStatement.setDate(22, null);
				preparedStatement.setInt(23, i);
				preparedStatement.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final String url = "jdbc:oracle:thin:@192.168.68.103:1579:gwdev";
	private static final String username = "ugw";
	private static final String passwd = "ugw";
	private static Connection koneksi;

	public static Connection getKoneksi() throws Exception {
		if (koneksi == null) {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			koneksi = DriverManager.getConnection(url, username, passwd);
		}
		return koneksi;
	}
}
