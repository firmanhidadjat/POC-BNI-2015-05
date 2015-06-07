package id.co.hanoman.bni.ws1;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface WSServerTest {
	public String sendSMS(@WebParam(name = "servName") String servName,
			@WebParam(name = "smsn_id") String smsn_id,
			@WebParam(name = "notelp") String notelp,
			@WebParam(name = "pesan") String pesan);
}
