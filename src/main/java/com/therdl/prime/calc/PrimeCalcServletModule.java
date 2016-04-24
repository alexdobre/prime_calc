package com.therdl.prime.calc;

import com.google.inject.servlet.ServletModule;
import com.therdl.prime.calc.api.DispatcherServlet;

/**
 * Guice dependency injection module for the prime numbers calculator service
 * For more information please see https://github.com/google/guice/wiki/GettingStarted
 * Created by Alex on 22/04/2016.
 */
public class PrimeCalcServletModule extends ServletModule {

	@Override
	protected void configureServlets() {
		bind(DispatcherServlet.class);
		serve("/v1/primeCalc").with(DispatcherServlet.class);
	}
}
