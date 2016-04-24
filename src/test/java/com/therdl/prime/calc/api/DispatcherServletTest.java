package com.therdl.prime.calc.api;

import com.therdl.prime.calc.alg.lucas.LucasAlg;
import com.therdl.prime.calc.alg.sieve.SieveAlg;
import com.therdl.prime.calc.model.Method;
import com.therdl.prime.calc.model.Request;
import com.therdl.prime.calc.model.Response;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static com.therdl.prime.calc.TestUtil.buildRequest;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Most of the functionality is tested in {@link com.therdl.prime.calc.PrimeCalcServiceTest}
 * Here we use mockito to test an internal server error
 * Created by Alex on 24/04/2016.
 */
public class DispatcherServletTest {

	@Test
	public void serverErrorLucasTest() throws TimeoutException, InterruptedException {
		//Given
		SieveAlg sieveAlg = mock(SieveAlg.class);
		LucasAlg lucasAlg = mock(LucasAlg.class);
		DispatcherServlet ds = new DispatcherServlet(sieveAlg, lucasAlg);
		Set<Error> errors = new HashSet<>();
		Map<String, Object> requestMap = buildRequest(Method.LUCAS_FILTER.name(), "121");
		Request request = new Request(requestMap);

		//When
		when(lucasAlg.process(any(int.class))).thenThrow(InterruptedException.class);
		Response rs = ds.processRequest(request, errors);

		//Then
		verify(lucasAlg, times(1)).process(any(int.class));
		verify(sieveAlg, never()).process(any(int.class));
		assertTrue("Result must be null", rs == null);
		assertTrue("Must have one error", errors.size() == 1);
		assertTrue("Error must be inner server", errors.contains(Error.SERVER_ERROR));
	}

	@Test
	public void serverErrorSieveTest() throws TimeoutException, InterruptedException {
		//Given
		SieveAlg sieveAlg = mock(SieveAlg.class);
		LucasAlg lucasAlg = mock(LucasAlg.class);
		DispatcherServlet ds = new DispatcherServlet(sieveAlg, lucasAlg);
		Set<Error> errors = new HashSet<>();
		Map<String, Object> requestMap = buildRequest(Method.SIEVE.name(), "121");
		Request request = new Request(requestMap);

		//When
		when(sieveAlg.process(any(int.class))).thenThrow(InterruptedException.class);
		Response rs = ds.processRequest(request, errors);

		//Then
		verify(lucasAlg, never()).process(any(int.class));
		verify(sieveAlg, times(1)).process(any(int.class));
		assertTrue("Result must be null", rs == null);
		assertTrue("Must have one error", errors.size() == 1);
		assertTrue("Error must be inner server", errors.contains(Error.SERVER_ERROR));
	}
}
