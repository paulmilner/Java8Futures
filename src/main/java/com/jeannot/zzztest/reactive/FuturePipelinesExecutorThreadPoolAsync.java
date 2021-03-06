package com.jeannot.zzztest.reactive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FuturePipelinesExecutorThreadPoolAsync {

	static ExecutorService pool; //Note that this is NOT a pool of daemon threads!!

	public static void main(String[] args) {
		
		System.out.println("Starting...");
		System.out.println("running main on thread: " + Thread.currentThread().getName());
		System.out.println("am I a daemon? " + Thread.currentThread().isDaemon());
		
		pool = Executors.newFixedThreadPool(10);
		
		FuturePipelinesExecutorThreadPoolAsync play1 = new FuturePipelinesExecutorThreadPoolAsync();
		play1.doSomeBusinessyStuffBetterAsync();
		
		pool.shutdown(); //You need to shut down the thread pool when you're done with it or the process will hang around...
		
		System.out.println("Finished.");
	}
	
	private void doSomeBusinessyStuffBetterAsync() {
		System.out.println("running doSomeBusinessyStuffBetter on thread: " + Thread.currentThread().getName());
		System.out.println("am I a daemon? " + Thread.currentThread().isDaemon());

		CompletableFuture<Void> done = CompletableFuture
			.supplyAsync(()->getSomethingFromDatabase1(),pool)
			.thenApplyAsync((resultOfPreviousStage)->getSomethingFromService2(resultOfPreviousStage),pool)
			.thenAcceptAsync((resultOfPreviousStage)->System.out.println("Result code=" + resultOfPreviousStage),pool);
		
		try {
			done.get(); //You have to block eventually, to ensure the JVM doesn't die before it's finished... I think... because of daemon threads?
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Task that potentially takes a while...
	 */
	private String getSomethingFromDatabase1() {
		System.out.println("running getSomethingFromDatabase1 takes a while... on thread: " + Thread.currentThread().getName());
		System.out.println("am I a daemon? " + Thread.currentThread().isDaemon());

		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String result = "DB result";
		return result;
	}

	/**
	 * Task that potentially takes a while...
	 */
	private Integer getSomethingFromService2(final String name) {
		System.out.println("running getSomethingFromService2(" + name + ") takes even longer on thread: " + Thread.currentThread().getName());
		System.out.println("am I a daemon? " + Thread.currentThread().isDaemon());
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Integer result = new Integer(7832);
		System.out.println("running getSomethingFromService2, returns " + result);
		return result;
	}
	
}