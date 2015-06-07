package id.co.hanoman.camel.poll;

import org.apache.camel.CamelContext;

public interface PollRuntime {

	int poll(CamelContext ctx, PollConsumer consumer);

}
