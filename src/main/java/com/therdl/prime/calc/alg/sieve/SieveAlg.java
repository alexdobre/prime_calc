package com.therdl.prime.calc.alg.sieve;

import com.google.inject.Singleton;
import com.therdl.prime.calc.alg.ConcurrentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Contains the Sieve_of_Eratosthenes algorithm for calculating prime numbers up to a certain limit
 * https://en.wikipedia.org/wiki/Sieve_of_Eratosthenes
 * Created by Alex on 23/04/2016.
 */
@Singleton
public class SieveAlg {

	private static final Logger log = LoggerFactory.getLogger(SieveAlg.class);

	/**
	 * The entry point to the algorithm
	 *
	 * @param limit the upper limit to the prime numbers search
	 * @return the list of prime numbers found
	 */
	public SortedSet<Integer> process(int limit) throws TimeoutException, InterruptedException {
		log.debug("SieveAlg process BEGIN");

		if (limit < 2) {
			return new TreeSet<>();
		}

		Sieve sieve = new Sieve(limit);
		Workers workers = new Workers(sieve);
		try {
			sieve.getLock().lock();
			log.debug("Starting workers, waiting for DONE signal");
			workers.start();
			if (!sieve.getDone().await(5, TimeUnit.SECONDS)) {
				log.debug("Timed out after 5 seconds ->");
				throw new TimeoutException("Processing timed out after 5 seconds");
			}
			log.debug("SieveAlg picked up DONE signal");

			log.debug("Stopping the workers");
			ConcurrentUtil.stop(workers.getExecutor());
		} finally {
			sieve.getLock().unlock();
		}

		return workers.getPrimes();
	}

}
