package com.therdl.prime.calc.alg.lucas;

import com.google.inject.Singleton;
import com.therdl.prime.calc.alg.ConcurrentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Contains the lucas numbers filter primality algorithm.
 * For more information please see: https://www.youtube.com/watch?v=lEvXcTYqtKU
 * Created by Alex on 23/04/2016.
 */
@Singleton
public class LucasAlg {

	private static final Logger log = LoggerFactory.getLogger(LucasAlg.class);

	/**
	 * The entry point to the algorithm
	 *
	 * @param limit the upper limit to the prime numbers search
	 * @return the list of prime numbers found
	 */
	public SortedSet<Integer> process(int limit) throws TimeoutException, InterruptedException {
		log.debug("LucasAlg process - BEGIN");
		//we hard code the first few primes
		SortedSet<Integer> primes = new TreeSet<>();
		if (limit < 2) {
			return primes;
		} else if (limit == 2) {
			primes.add(2);
			return primes;
		} else if (limit == 3 || limit == 4) {
			primes.add(2);
			primes.add(3);
			return primes;
		}

		Lucas lucas = new Lucas(limit);
		Workers workers = new Workers(lucas);

		try {
			workers.getLock().lock();
			log.debug("Starting workers, waiting for DONE signal");
			lucas.start();
			workers.start();
			if (!workers.getDone().await(5, TimeUnit.SECONDS)) {
				log.debug("Timed out after 5 seconds ->");
				throw new TimeoutException("Processing timed out after 5 seconds");
			}
			log.debug("SieveAlg picked up DONE signal");

			log.debug("Stopping the workers");
			ConcurrentUtil.stop(workers.getExecutor());
		} finally {
			workers.getLock().unlock();
		}

		return workers.getPrimes();
	}
}
