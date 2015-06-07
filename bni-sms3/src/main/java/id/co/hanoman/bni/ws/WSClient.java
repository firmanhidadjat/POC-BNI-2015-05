package id.co.hanoman.bni.ws;

import id.co.hanoman.U;

import java.util.List;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSClient {
	private static final Logger LOG = LoggerFactory.getLogger(WSClient.class);

	public WSClient() {
	}

	public void dodol(Exchange d) {
		List<List<String>> dd = (List<List<String>>) d.getIn().getBody();

//		LOG.info("GGGGGGGGG " + U.dump(d.getIn().getBody()));
		// + dd.get(0).get(2) + " " + dd.get(0).get(3) + " "
		// + dd.get(0).get(4) + " " + dd.get(0).get(5) + " "
		// + dd.get(0).get(6));
		d.getIn().setBody(d.getIn().getBody(List.class).get(0));
	}
}
