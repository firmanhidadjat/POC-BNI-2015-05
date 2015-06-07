package id.co.hanoman.camel.poll;

import java.util.concurrent.TimeUnit;

import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DBPool consumer.
 */
public class PollConsumer extends ScheduledPollConsumer {
	private static final Logger LOG = LoggerFactory.getLogger(PollConsumer.class);
    private final PollEndpoint endpoint;
    long nextrun = 0;
    long idleDelay;
    PollRuntime runtime;

    public PollConsumer(PollEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }
    
    
    @Override
    protected void doStart() throws Exception {
    	setTimeUnit(TimeUnit.MILLISECONDS);
    	setDelay(endpoint.getDelay());
    	setInitialDelay(endpoint.getInitialDelay());
    	idleDelay = endpoint.getIdleDelay();
    	runtime = endpoint.getRuntime();
    	super.doStart();
    }

    @Override
    protected int poll() throws Exception {
    	if (nextrun > 0) {
    		if (nextrun > System.currentTimeMillis()) return 0;
    	}
		int res = runtime.poll(getEndpoint().getCamelContext(), this);
    	if (res == 0) {
        	if (LOG.isDebugEnabled()) LOG.info("GO IDLE "+idleDelay);
        	nextrun = System.currentTimeMillis() + idleDelay;
    	} else {
    		nextrun = 0;
    	}
        return res;
    }
}
