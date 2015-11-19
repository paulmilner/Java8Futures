package com.jeannot.zzztest.reactive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongUsage {

	private static final int POOLSIZE = 10;
	
	static ExecutorService pool;
	
	static final AtomicLong value = new AtomicLong(0L);
	static long nonAtomicValue = 0L;
	static String build = "BUILD:";

	public static void main(String[] args) {
		
		System.out.println("Starting...");
		pool = Executors.newFixedThreadPool(POOLSIZE);
		
		List<Callable<Long>> callables = new ArrayList<>();
		for (int i=0; i<POOLSIZE; i++) {
			Incrementor inc = new Incrementor(""+i);
			callables.add(inc);
		}
		try {
			pool.invokeAll(callables);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		pool.shutdown(); //You need to shut down the thread pool when you're done with it or the process will hang around...
		System.out.println("Finished.");
	}
	
	
	/**
	 * This incremements a non-atomic value, and an atomic value, to see what happens... 
	 * Not sure this actually proves anything useful, mind...
	 * 
	 */
	private static class Incrementor implements Callable<Long> {
		
		private String input;
		
		Incrementor(String input) {
			this.input = input;
		}

		@Override
		public Long call() {
			long incrementedValue = value.incrementAndGet();
			nonAtomicValue++;
			build += input;
			System.out.println("Value is now: " + incrementedValue + ", non-atomic=" + nonAtomicValue + ", build=" + build + " says " + Thread.currentThread().getName());
			return incrementedValue;
		}
		
	}
	
	
}