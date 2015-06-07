package id.co.hanoman.bni.ws1;

import id.co.hanoman.U;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import oracle.jdbc.pool.OracleDataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBInsert {
	private static final Logger LOG = LoggerFactory.getLogger(DBInsert.class);
	Connection conn = null;

	private static String namaServer;

	public DBInsert() {
		this.bacaFile();
	}

	public String insertDB(CamelContext ctx, Exchange exchange) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (conn == null) {
				LOG.info("connecting to db "
						+ ctx.getRegistry()
								.findByTypeWithName(DataSource.class));
				OracleDataSource ds = ctx.getRegistry()
						.findByTypeWithName(OracleDataSource.class)
						.get("dataSource");
				conn = ds.getConnection();
				conn.setAutoCommit(false);
				LOG.info("connected");
			}

			// LOG.info("========================  "
			// + U.dump(exchange.getIn().getBody()));

			ps = conn
					.prepareStatement("insert into ugw.RTL_SMSN_TEST_2(NARRATIVE_1, SMSN_ID, NO_HP, NARRATIVE_3, SERVER) values (?,?,?,?,?)");

			List a = (List) exchange.getIn().getBody();

			ps.setString(1, (String) a.get(3));
			ps.setLong(2, Long.parseLong(String.valueOf(a.get(1))));
			ps.setString(3, (String) a.get(2));
			ps.setString(4, (String) a.get(3));
			ps.setString(5, this.namaServer);
			ps.executeUpdate();
			conn.commit();
			ps.close();

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e2) {
				}
				conn = null;
			}
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception e) {
				}
			}
			// if (conn != null) {
			// try {
			// conn.close();
			// } catch (Exception e) {}
			// }
		}
//		return "0";
		return this.namaServer;
	}

	public void bacaFile() {
		String namaFile = "/home/devadm/namaServer";
		BufferedReader br = null;
		String stringHasil = "";

		try {
			File f = new File(namaFile);
			if (f.isFile()) {
				String sCurrentLine;
				br = new BufferedReader(new FileReader(f));
				while ((sCurrentLine = br.readLine()) != null) {
					stringHasil = stringHasil + sCurrentLine;
				}
			} else {
				stringHasil = "LOCAL";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		this.namaServer = stringHasil;
	}

}
