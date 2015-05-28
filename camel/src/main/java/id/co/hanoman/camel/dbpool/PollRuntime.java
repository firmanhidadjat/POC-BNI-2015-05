package id.co.hanoman.camel.dbpool;

import org.apache.camel.CamelContext;

public interface PollRuntime {

	int pool(CamelContext ctx, PollConsumer consumer);

}
