package com.jeannot.zzztest.reactive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Combining results... TODO...
 * 
 */
public class FuturePipelinesCombinations {

	public static void main(String[] args) {
		
		System.out.println("Starting...");
		
		FuturePipelinesCombinations play1 = new FuturePipelinesCombinations();
		play1.doStuff();
		
		System.out.println("Finished.");
	}
	
	private void doStuff() {
		System.out.println("running doSomeBusinessyStuffBetter on thread: " + Thread.currentThread().getName());
		System.out.println("am I a daemon? " + Thread.currentThread().isDaemon());

		CompletableFuture<Void> done = CompletableFuture
			.supplyAsync(()->getSomethingFromDatabase1())
			.thenApply((resultOfPreviousStage)->getSomethingFromService2(resultOfPreviousStage))
			.thenAccept((resultOfPreviousStage)->System.out.println("Result code=" + resultOfPreviousStage));
		
		try {
			done.get(); //You have to block eventually, to ensure the JVM doesn't die before it's finished... I think... because of daemon threads...
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
