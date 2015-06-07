package id.co.hanoman.bni.sms;

import id.co.hanoman.camel.poll.PollConsumer;
import id.co.hanoman.camel.poll.PollRuntime;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBPool implements PollRuntime {
	private static final Logger LOG = LoggerFactory.getLogger(DBPool.class);
	Connection conn = null;

	@Override
	public int pool(PollConsumer consumer) {
		PreparedStatement ps = null, ps1 = null;
		ResultSet rs = null;
		try {
			if (conn == null) {
				LOG.info("connecting to db");
				conn = DriverManager.getConnection("jdbc:mysql://10.211.55.4:3306/bnisms", "seno", "dodol123");
				conn.setAutoCommit(false);
				LOG.info("connected");
			}
			
			ps = conn.prepareStatement("select id, text from push_sms where task_status is null order by id asc limit 10 for update");
			rs = ps.executeQuery();
			
			Map<Long, PushSMS> data = new LinkedHashMap<Long, PushSMS>();
			while (rs.next()) {
				long id = rs.getLong(1);
				data.put(id, new PushSMS(id, rs.getString(2)));
			}
			
			LOG.info("DATA "+data);
			
			ps1 = conn.prepareStatement("update push_sms set task_status = ?, task_timestamp = ? where id = ?");
			for (long l : data.keySet()) {
				ps1.setString(1, "inprogress");
				ps1.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				ps1.setLong(3, l);
				ps1.executeUpdate();
			}
			
			conn.commit();
			
			for (PushSMS obj : data.values()) {
				try {
					Exchange exchange = consumer.getEndpoint().createExchange();
					exchange.setProperty("id.co.hanoman.bni.sms.PushSMS", obj);
					exchange.getIn().setBody(obj);;
					consumer.getProcessor().process(exchange);
					
					PushSMS res = (PushSMS) exchange.getOut().getBody();
					if (res == null) res = (PushSMS) exchange.getIn().getBody();
					LOG.info("OUT "+res);
					
					ps1.setString(1, res.getStatus());
					ps1.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
					ps1.setLong(3, obj.getId());
					ps1.executeUpdate();
					conn.commit();
				} catch (Exception e) {
					ps1.setString(1, "failed");
					ps1.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
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
				} catch (Exception e2) {}
				conn = null;
			}
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
//			if (conn != null) {
//				try {
//					conn.close();
//				} catch (Exception e) {}
//			}
		}
		return 0;
	}

}
