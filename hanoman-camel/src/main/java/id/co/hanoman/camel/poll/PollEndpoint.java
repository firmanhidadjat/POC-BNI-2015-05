package id.co.hanoman.camel.poll;

import id.co.hanoman.camel.HanomanComponent;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * Represents a DBPool endpoint.
 */
public class PollEndpoint extends DefaultEndpoint {
//	private static final Logger LOG = LoggerFactory.getLogger(PollEndpoint.class);
	
	long delay = 100;
	long initialDelay = 1000;
	long idleDelay = 5000;
	PollRuntime runtime;

    public PollEndpoint() {
    }
    
    public PollEndpoint(String uri, HanomanComponent component) {
        super(uri, component);
    }

    @SuppressWarnings("deprecation")
	public PollEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public Producer createProducer() throws Exception {
        return null;
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new PollConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public long getInitialDelay() {
		return initialDelay;
	}

	public void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
	}

	public long getIdleDelay() {
		return idleDelay;
	}

	public void setIdleDelay(long idleDelay) {
		this.idleDelay = idleDelay;
	}
	
	public PollRuntime getRuntime() {
		return runtime;
	}
	
	public void setRuntime(PollRuntime runtime) {
		this.runtime = runtime;
	}
}
