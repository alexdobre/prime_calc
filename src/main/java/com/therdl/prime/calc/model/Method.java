package com.therdl.prime.calc.model;

import java.util.Arrays;

/**
 * Contains the available methods to calculate prime numbers
 * Created by Alex on 22/04/2016.
 */
public enum Method {

	/**
	 * Represents the Sieve_of_Eratosthenes algorithm: https://en.wikipedia.org/wiki/Sieve_of_Eratosthenes
	 */
	SIEVE,

	/**
	 * An algorithm that uses the Lucas primality test: https://en.wikipedia.org/wiki/Lucas_primality_test
	 */
	LUCAS_FILTER;

	public static String printMethods() {
		StringBuilder sb = new StringBuilder();
		Arrays.stream(Method.values()).forEach(v -> sb.append(v.name()).append(','));
		//remove last comma
		return sb.substring(0, sb.length() - 1);
	}
}
