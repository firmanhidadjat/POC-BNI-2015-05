package id.co.hanoman.tcp.server;

import id.co.hanoman.bni.message.Iso8583;

import org.apache.camel.Exchange;

public class DummyServer {

	public void fooService(Exchange exchange) throws Exception {
		Iso8583 req = exchange.getIn().getBody(Iso8583.class);
		req.setResponseCode("00");
		exchange.getOut().setBody(req);
	}

}
