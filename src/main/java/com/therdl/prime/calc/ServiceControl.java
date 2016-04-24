package com.therdl.prime.calc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * This serves as the entry point class. it holds the main method, it initializes the GUICE module and starts the
 * Jetty servlet
 * Created by Alex on 22/04/2016.
 */
public class ServiceControl {

	private static final Logger log = LoggerFactory.getLogger(ServiceControl.class);

	public static void main(String[] args) {
		PrimeCalcServletModule servletModule = new PrimeCalcServletModule();
		//we create the injector before the server start
		Injector injector = Guice.createInjector(servletModule);
		Server server = initServer();
		try {
			//wait until the server exits
			server.join();

			//start the server
			server.start();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private static Server initServer() {
		QueuedThreadPool threadPool = new QueuedThreadPool(20, 1);
		Server server = new Server(threadPool);

		ServerConnector http = new ServerConnector(server);
		http.setPort(8080);
		http.setIdleTimeout(500000);

		server.setConnectors(new Connector[]{http});

		ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/",
				ServletContextHandler.SESSIONS);
		servletContextHandler.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));

		// We must add DefaultServlet or our server will always return 404s
		servletContextHandler.addServlet(DefaultServlet.class, "/");

		return server;
	}
}
