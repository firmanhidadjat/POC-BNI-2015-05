package demo;

import id.co.hanoman.U;
import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;

import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.transport.WebServiceConnection;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpServletConnection;

@Endpoint
public class CountryEndpoint {
	private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";
	private static final Logger log = LoggerFactory.getLogger(CountryEndpoint.class);

	private CountryRepository countryRepository;

	@Autowired
	public CountryEndpoint(CountryRepository countryRepository) {
		log.info("START ENDPOINT");
		this.countryRepository = countryRepository;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")
	@ResponsePayload
	public GetCountryResponse getCountry(@RequestPayload GetCountryRequest request, SoapHeader soapHeader) {
		WebServiceConnection wsConn = TransportContextHolder.getTransportContext().getConnection();
		if (wsConn instanceof HttpServletConnection) {
			HttpServletConnection httpConn = (HttpServletConnection) wsConn;
			for (Enumeration<String> headerNames = httpConn.getHttpServletRequest().getHeaderNames(); headerNames.hasMoreElements(); ) {
				String hn = headerNames.nextElement();
				log.info("HTTP HEADER ["+hn+"]  =  ["+httpConn.getHttpServletRequest().getHeader(hn)+"]");
			}
		} else {
			log.info("WS CONNECTION "+wsConn);
		}
		log.info("SOAP HEADER "+U.dump(soapHeader.getSource()));
		GetCountryResponse response = new GetCountryResponse();
		response.setCountry(countryRepository.findCountry(request.getName()));

		return response;
	}
}