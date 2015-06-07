package id.co.hanoman.ws;

import javax.xml.ws.Endpoint;

public class RunWSTest {
	public static void main(String[] sbargrgf) {
		String endpoint = "http://0.0.0.0:7001/BNIWSTest";
		System.out.println("Menjalankan WS...");
		Endpoint.publish(endpoint, new WSTest());
		System.out.println("WS jalan... endpoint: " + endpoint);
	}
}
