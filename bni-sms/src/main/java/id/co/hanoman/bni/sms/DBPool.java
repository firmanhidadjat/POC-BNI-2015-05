package id.co.hanoman.bni.sms;

import id.co.hanoman.camel.poll.PollConsumer;
import id.co.hanoman.camel.poll.PollRuntime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import oracle.jdbc.pool.OracleDataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBPool implements PollRuntime {
	private static final Logger LOG = LoggerFactory.getLogger(DBPool.class);
	Connection conn = null;

	private static String namaServer;

	public DBPool() {
		 this.bacaFile();
	}
	
	@Override
	public int poll(CamelContext ctx, PollConsumer consumer) {
		PreparedStatement ps = null, ps1 = null;
		ResultSet rs = null;
		try {
			if (conn == null) {
				LOG.info("connecting to db "
						+ ctx.getRegistry()
								.findByTypeWithName(DataSource.class));
				OracleDataSource ds = ctx.getRegistry()
						.findByTypeWithName(OracleDataSource.class)
						.get("dataSource");
				// conn =
				// DriverManager.getConnection("jdbc:mysql://10.211.55.4:3306/bnisms",
				// "seno", "dodol123");
				// conn =
				// DriverManager.getConnection("jdbc:oracle:thin:@192.168.68.103:1579/gwdev",
				// "ugw", "ugw");
				conn = ds.getConnection();
				conn.setAutoCommit(false);
				LOG.info("connected");
			}

			// ps =
			// conn.prepareStatement("select id, text from push_sms where task_status is null order by id asc limit 10 for update");
			ps = conn
					.prepareStatement("select SMSN_ID, ACCT_NO, no_hp, narrative_3  from ugw.rtl_smsn_test where DELIVERED_PHONE is null and rownum <= 5 for update");
			rs = ps.executeQuery();

			Map<Long, PushSMS> data = new LinkedHashMap<Long, PushSMS>();
			while (rs.next()) {
				long id = rs.getLong(1);
				data.put(id, new PushSMS(id, rs.getString(2), rs.getString(3),
						rs.getString(4)));
			}

			LOG.info("DATA " + data);

			ps1 = conn
					.prepareStatement("update ugw.rtl_smsn_test set DELIVERED_PHONE = ?, DELIVERED_TIME = ?, NARRATIVE_1 = ? where SMSN_ID = ?");
			for (long l : data.keySet()) {
				ps1.setString(1, "inprogress");
				ps1.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				ps1.setString(3, namaServer);
				ps1.setLong(4, l);
				ps1.executeUpdate();
			}

			conn.commit();
			ps1.close();
			ps1 = conn
					.prepareStatement("update ugw.rtl_smsn_test set DELIVERED_PHONE = ?, DELIVERED_TIME = ? where SMSN_ID = ?");

			for (PushSMS obj : data.values()) {
				try {
					Exchange exchange = consumer.getEndpoint().createExchange();
					exchange.setProperty("pushSMS", obj);
					exchange.getIn().setBody(
							new String[] { namaServer, String.valueOf(obj.getId()),
									obj.getNotelp(), obj.getPesan() });
					consumer.getProcessor().process(exchange);
					if (exchange.getException() != null) throw exchange.getException();
					MessageContentsList res = (MessageContentsList) exchange
							.getOut().getBody();
					if (res == null)
						res = (MessageContentsList) exchange.getIn().getBody();
					LOG.info("OUT " + res);

					ps1.setString(1, String.valueOf(res.get(0)));
					ps1.setTimestamp(2,
							new Timestamp(System.currentTimeMillis()));
					ps1.setLong(3, obj.getId());
					ps1.executeUpdate();
					conn.commit();
				} catch (Exception e) {
					e.printStackTrace();
					ps1.setString(1, "failed");
					ps1.setTimestamp(2,
							new Timestamp(System.currentTimeMillis()));
					ps1.setLong(3, obj.getId());
					ps1.executeUpdate();
					conn.commit();
				}
			}

			conn.commit();
			return data.size();
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
			if (ps1 != null) {
				try {
					ps1.close();
				} catch (Exception e) {
				}
			}
			// if (conn != null) {
			// try {
			// conn.close();
			// } catch (Exception e) {}
			// }
		}
		return 0;
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
