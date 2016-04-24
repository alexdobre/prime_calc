package com.therdl.prime.calc.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.therdl.prime.calc.alg.lucas.LucasAlg;
import com.therdl.prime.calc.alg.sieve.SieveAlg;
import com.therdl.prime.calc.model.*;
import flexjson.JSONDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * This servlet receives the REST messages over HTTP and routes them to the appropriate algorithm.
 * Please see {@link RequestField} and {@link ResponseField} for the message format.
 * <p/>
 * DispatcherServlet uses Guice to implement  the command pattern re Gang of 4 design patterns
 * see http://java.dzone.com/articles/design-patterns-command
 * Created by Alex on 22/04/2016.
 */
@Singleton
public class DispatcherServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

	private SieveAlg sieveAlg;
	private LucasAlg lucasAlg;

	@Inject
	public DispatcherServlet(SieveAlg sieveAlg, LucasAlg lucasAlg) {
		this.sieveAlg = sieveAlg;
		this.lucasAlg = lucasAlg;
	}

	@Override
	protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws ServletException, IOException {
		log.debug("DispatcherServlet doPost BEGIN");
		Request request = null;
		Response response = null;
		Set<Error> errors = new HashSet<>();
		try {
			request = readRequest(httpRequest);

		} catch (IOException | ClassCastException e) {
			log.error(e.getMessage(), e);
			errors.add(Error.BAD_REQUEST);
		}

		if (request != null) {
			errors.addAll(validateRequest(request));
		} else {
			errors.add(Error.BAD_REQUEST);
		}

		if (request != null && errors.isEmpty()) {
			response = processRequest(request, errors);
		}

		if (!errors.isEmpty()) {
			response = createErrorResponse(errors);
		}
		writeResponse(httpResponse, response);

		log.debug("DispatcherServlet doPost END");
	}

	public Response processRequest(Request request, Set<Error> errors) {
		Response response = null;
		Integer limit = new Integer(request.getContents().get(RequestField.LIMIT.getLabel()).toString());
		Method method = Method.valueOf(request.getContents().get(RequestField.METHOD.getLabel()).toString());
		SortedSet<Integer> primes = null;

		try {
			if (Method.SIEVE.equals(method)) {
				primes = sieveAlg.process(limit);
			} else if (Method.LUCAS_FILTER.equals(method)) {
				primes = lucasAlg.process(limit);
			}

			response = createRespose(request, primes);

		} catch (TimeoutException e) {
			log.error(e.getMessage(), e);
			errors.add(Error.TIMEOUT_ERROR);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			errors.add(Error.SERVER_ERROR);
		}
		return response;
	}

	@Override
	protected void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws ServletException, IOException {
		log.debug("DispatcherServlet -> doGet");
		Set<Error> errors = new TreeSet<>();
		errors.add(Error.GET_NOT_SUPPORTED);
		Response response = createErrorResponse(errors);
		writeResponse(httpResponse, response);
	}

	private Set<Error> validateRequest(Request request) {
		Set<Error> errorSet = new HashSet<>();
		Arrays.stream(RequestField.values()).forEach(
				rf -> {
					if (!rf.isValid(request.getContents().get(rf.getLabel()))) {
						errorSet.add(rf.getError());
					}
				});
		return errorSet;
	}

	private Request readRequest(HttpServletRequest httpRequest) throws IOException {
		StringBuilder payload = new StringBuilder();
		BufferedReader reader = httpRequest.getReader();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				payload.append(line).append('\n');
			}
		} finally {
			reader.close();
		}

		Map<String, Object> requestContents;

		requestContents = new JSONDeserializer<Map<String, Object>>().deserialize(payload.toString());


		return new Request(requestContents);
	}

	private Response createRespose(Request request, SortedSet<Integer> primes) {
		Response response = new Response();
		Map<String, Object> responseMap = new HashMap<>();

		responseMap.put(ResponseField.LIMIT.getLabel(), request.getContents().get(RequestField.LIMIT.getLabel()));
		responseMap.put(ResponseField.PRIMES.getLabel(), primes);
		response.setContents(responseMap);
		return response;
	}

	private Response createErrorResponse(Set<Error> errors) {
		Response response = new Response();
		if (errors.contains(Error.SERVER_ERROR)) {
			response.setStatusCode(Response.SERVER_ERROR);
		} else {
			response.setStatusCode(Response.BAD_REQUEST);
		}
		Map<String, Object> respContents = new HashMap<>();
		respContents.put(ResponseField.ERROR.getLabel(),
				errors.stream().map(Error::getLabel).collect(Collectors.toList()));
		response.setContents(respContents);
		return response;
	}

	private void writeResponse(HttpServletResponse httpResponse, Response response) throws IOException {
		httpResponse.setStatus(response.getStatusCode());

		PrintWriter out = httpResponse.getWriter();
		out.println(response.getPayload());
		out.flush();
		out.close();
	}
}
