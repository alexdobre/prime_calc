package com.therdl.prime.calc.alg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Utility methods to help with concurrency
 * Created by Alex on 23/04/2016.
 */
public class ConcurrentUtil {

	/**
	 * Elegantly shuts down an executor
	 *
	 * @param executor the executor to shut down
	 */
	public static void stop(ExecutorService executor) {
		try {
			executor.shutdown();
		} finally {
			executor.shutdownNow();
		}
	}

	/**
	 * Utility method for one thread to signal done based on a lock and condition pair
	 *
	 * @param lock the lock
	 * @param done the condition
	 */
	public static void signalDone(Lock lock, Condition done) {
		try {
			lock.lock();
			done.signal();
		} finally {
			lock.unlock();
		}
	}
}
