package com.therdl.prime.calc.alg.sieve;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Wraps around the worker threads acting on the sieve and provides convenience methods
 * Created by Alex on 23/04/2016.
 */
public class Workers {

	private static final Logger log = LoggerFactory.getLogger(Workers.class);

	private Sieve sieve;

	//at any point in time this is the lowest composite number that has been removed from the sieve across
	// the worker threads
	private AtomicInteger lowestComposite = new AtomicInteger(0);

	//this map tracks each active worker thread's progress, using this map we can calculate the lowest composite
	//at a point in time
	private Map<Integer, Integer> workerProgress = new ConcurrentHashMap<>();

	private SortedSet<Integer> primes = Collections.synchronizedSortedSet(new TreeSet<>());

	private ExecutorService executor = Executors.newWorkStealingPool();

	public Workers(Sieve sieve) {
		this.sieve = sieve;
	}

	/**
	 * Starts work on the given sieve. The workers remove non primes from the {@link Sieve} until only the primes
	 * are left.
	 */
	public void start() {
		primes.add(2);
		scheduleWork(2);
	}

	/**
	 * @return the primes discovered by the workers
	 */
	public SortedSet<Integer> getPrimes() {
		return primes;
	}

	/**
	 * @return the thread executor
	 */
	public ExecutorService getExecutor() {
		return executor;
	}

	/**
	 * Schedules work on the sieve given the prime number starting point. Work only starts if the prime given is low
	 * enough to be relevant
	 *
	 * @param prime the prime number starting point
	 */
	private void scheduleWork(int prime) {
		if (prime <= (sieve.getLimit())) {
			executor.submit(() -> {
				log.debug("Starting prime worker thread: {}", prime);
				int toRemove = prime;
				while ((sieve.getLimit() - toRemove) >= prime) {
					sieve.remove(toRemove);
					log.debug("Prime worker thread: {} removed {}", prime, toRemove);
					workerProgress.put(prime, toRemove);
					//we keep checking for primes if there are threads available
					if (workerProgress.size() < Runtime.getRuntime().availableProcessors()) {
						log.debug("Checking for primes to work because we have {} threads / {} processors",
								workerProgress.size(), Runtime.getRuntime().availableProcessors());
						checkForPrime();
					}
					toRemove += prime;
				}

				//we call remove one more time for the final value of toRemove
				sieve.remove(toRemove);
				log.debug("Prime worker thread: {} finally removed {} because it's just below the limit ",
						prime, toRemove, sieve.getLimit());
				//before the worker exits we remove it from the progress map
				workerProgress.remove(prime);
				log.debug("Finishing prime worker thread: {}, threads active {}", prime, workerProgress.size());
				//we check for prime here just in case all the scheduled threads finished without spawning new necessary
				//prime threads
				log.debug("checkForPrime ... end of thread: {}", prime);
				checkForPrime();
			});
		}
	}

	private synchronized void checkForPrime() {
		calculateLowestComposite();
		log.debug("current lowest composite: {}, lowest prime candidate: {}", lowestComposite,
				sieve.getLowestPrimeCandidate().get());
		if (sieve.getLowestPrimeCandidate().get() <= lowestComposite.get()) {
			if (primes.add(sieve.getLowestPrimeCandidate().get())) {
				scheduleWork(sieve.getLowestPrimeCandidate().get());
			}
		}
	}

	public synchronized void calculateLowestComposite() {
		lowestComposite.set(sieve.getLimit());
		workerProgress.forEach((k, v) -> {
			if (v < lowestComposite.get()) {
				lowestComposite.set(v);
			}
		});

	}

}
