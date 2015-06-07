package id.co.hanoman.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(serviceName = "POCBNI", targetNamespace = "http://bni.com/echo")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class WSTest {
	private static long counter;
	private static long waktu;

	@WebMethod(operationName = "sendSMS")
	@WebResult(name = "sendSMSResult")
	public String echo(@WebParam(name = "servName") String servName,
			@WebParam(name = "smsn_id") String id,
			@WebParam(name = "notelp") String notelp,
			@WebParam(name = "pesan") String pesan) {

		if (waktu == 0) {
			waktu = System.currentTimeMillis();
		}
		synchronized (WSTest.class) {
			System.out.println("| " + servName + " | " + id + " | " + notelp
					+ " | " + pesan + " | " + (++counter) + " | "
					+ ((System.currentTimeMillis() - waktu) / 1000));
		}
		return "ok";
	}
}
