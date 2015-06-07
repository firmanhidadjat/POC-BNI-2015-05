package id.co.hanoman.tcp.server;

import org.apache.camel.spring.SpringRouteBuilder;

public class TCPServerRouteBuilder extends SpringRouteBuilder {
//	private static Logger LOG = LoggerFactory.getLogger(TCPServerRouteBuilder.class);

	@Override
	public void configure() throws Exception {
//		<bean id="connInfo" class="id.co.hanoman.netty.ConnectionInfo" />
//
//		<bean id="codexFactory" class="id.co.hanoman.codex.ResourceCodexFactory" />
//
//		<bean id="valueHandler" class="id.co.hanoman.codex.PojoValueHandler">
//			<constructor-arg index="0" value="#{codexFactory}"/>
//		</bean>
//
//		<bean id="iso8583Codex" class="id.co.hanoman.codex.netty.NettyCodex">
//			<property name="codexFactory" value="#{codexFactory}" />
//			<property name="codex" value="iso8583" />
//			<property name="valueHandler" value="#{valueHandler}" />
//		</bean>
//
//		<bean id="dummyServer" class="id.co.hanoman.tcp.server.DummyServer" />
		
		from("netty:tcp://localhost:5050?decoders=#connInfo,#iso8583Codex&amp;encoders=#iso8583Codex&amp;sync=true&amp;disconnect=true")
			.to("log:msgin?showAll=true&amp;multiline=true&amp;level=INFO")
			.bean(DummyServer.class, "fooService")
			.to("log:msgout?showAll=true&amp;multiline=true&amp;level=INFO");
	}

}
