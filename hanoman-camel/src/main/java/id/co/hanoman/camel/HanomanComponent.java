package id.co.hanoman.camel;

import id.co.hanoman.camel.poll.PollEndpoint;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

/**
 * Represents the component that manages {@link HanomanEndpoint}.
 */
public class HanomanComponent extends DefaultComponent {

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
    	if (uri.startsWith("hanoman://poll")) {
    		Endpoint endpoint = new PollEndpoint(uri, this);
    		setProperties(endpoint, parameters);
    		return endpoint;
    	}
    	return null;
    }
}
