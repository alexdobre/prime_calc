package com.therdl.prime.calc.api;

import com.therdl.prime.calc.model.Method;

/**
 * Here reside all the errors that can be returned by the prime calc service
 * Created by Alex on 23/04/2016.
 */
public enum Error {

	SERVER_ERROR("Server side error, please chastise the developer :)"),
	TIMEOUT_ERROR("Processing timed out after 5 seconds, please lower the limit or run on a more powerful machine"),
	BAD_REQUEST("The request format must be JSON and specify a method and a limit"),
	BAD_METHOD("The method is required in the request and must be one of: "
			+ Method.printMethods() + " with no whitespace"),
	BAD_LIMIT("The limit is required in the request and  must be a positive integer between 0 and "
			+ (Integer.MAX_VALUE - 5)
			+ " written as a string with no whitespace"),
	GET_NOT_SUPPORTED("The HTTP GET method is not supported, please use POST");

	private String msg;

	Error(String msg) {
		this.msg = msg;
	}

	public String getLabel() {
		return this.name() + ": " + msg;
	}
}
