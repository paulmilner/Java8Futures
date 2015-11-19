package com.jeannot.zzztest.reactive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Play1 {

	static ExecutorService pool; //Note that this is NOT a pool of daemon threads!!

	public static void main(String[] args) {
		
		System.out.println("Starting...");
		System.out.println("running main on thread: " + Thread.currentThread().getName());
		System.out.println("am I a daemon? " + Thread.currentThread().isDaemon());
		
		pool = Executors.newFixedThreadPool(10);
		
		Play1 play1 = new Play1();
		//play1.doSomeBusinessyStuffBetter();
		play1.doSomeBusinessyStuffBetterAsync();
		
		pool.shutdown(); //You need to shut down the thread pool when you're done with it or the process will hang around...
		
		System.out.println("Finished.");
	}
	
	private void doSomeBusinessyStuff() {
		
		CompletableFuture<Void> done = getSomethingFromDatabase1Async() //do first stage, returns a future which, when complete causes the resulting string to be passed to the next stage
		.thenApply(this::getSomethingFromService2Async) //call service, returning Integer future to be passed to next stage
		.thenAccept(System.out::println); //this sysouts a CompletableFuture - not the Integer that getSomethingFromService2 returned... why?

	}

	private void doSomeBusinessyStuffBetter() {
		System.out.println("running doSomeBusinessyStuffBetter on thread: " + Thread.currentThread().getName());
		System.out.println("am I a daemon? " + Thread.currentThread().isDaemon());

		CompletableFuture<Void> done = CompletableFuture
			.supplyAsync(()->getSomethingFromDatabase1())
			.thenApply((resultOfPreviousStage)->getSomethingFromService2(resultOfPreviousStage))
			.thenAccept((resultOfPreviousStage)->System.out.println("Result code=" + resultOfPreviousStage));
		
		try {
			done.get(); //You have to block eventually, to ensure the JVM doesn't die before it's finished... I think... because of daemon threads?
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
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

	private void doSomeBusinessyStuffAsync() {
		
			CompletableFuture<Void> done = getSomethingFromDatabase1Async() //do first stage, returns a future which, when complete causes the resulting string to be passed to the next stage
			.thenApplyAsync(this::getSomethingFromService2Async,pool) //call service, returning Integer future to be passed to next stage
			.thenAcceptAsync(System.out::println,pool); //this sysouts a CompletableFuture - not the Integer that getSomethingFromService2 returned... why?
			try {
				done.get(); //You HAVE to call get() at SOME point... to wait for it all (if any left) to finish?
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
	
	/**
	 * Task that potentially takes a while...
	 */
	private CompletableFuture<String> getSomethingFromDatabase1Async() {
		System.out.println("running getSomethingFromDatabase1Async takes a while... on thread: " + Thread.currentThread().getName());
		System.out.println("am I a daemon? " + Thread.currentThread().isDaemon());
		final CompletableFuture<String> f = new CompletableFuture<>();

		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String result = "DB result";
		System.out.println("running getSomethingFromDatabase1Async, returns " + result);
		f.complete(result);
		return f;
	}

	/**
	 * Task that potentially takes a while...
	 */
	private CompletableFuture<Integer> getSomethingFromService2Async(final String name) {
		System.out.println("running getSomethingFromService2Async(" + name + ") takes even longer on thread: " + Thread.currentThread().getName());
		System.out.println("am I a daemon? " + Thread.currentThread().isDaemon());
		final CompletableFuture<Integer> f = new CompletableFuture<>();
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Integer result = new Integer(7832);
		System.out.println("running getSomethingFromService2Async, returns " + result);
		f.complete(result);
		return f;
	}
}
	
class BusinessName {
	final String firstName;
	final String lastName;
	final int age;
	
	public BusinessName(String firstName, String lastName, int age) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public int getAge() {
		return age;
	}
	
	
}
