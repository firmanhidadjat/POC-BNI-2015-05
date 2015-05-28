package id.co.hanoman.h2hpajak;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public class HelloWorldService {
 
	@WebMethod 
	public String getHelloWorldAsString(@WebParam(name="firstName") String firstName, @WebParam(name="lastName") String lastName) {
		return "XXX";
	}
 
}