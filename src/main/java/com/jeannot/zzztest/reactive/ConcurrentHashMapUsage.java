package com.jeannot.zzztest.reactive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentHashMapUsage {

	private static final int POOLSIZE = 1000;
	
	static ExecutorService pool;

	static final ConcurrentMap<String, Object> map = new ConcurrentHashMap<String,Object>();

	public static void main(String[] args) {
		
		System.out.println("Starting...");
		pool = Executors.newFixedThreadPool(POOLSIZE);
		
		List<Callable<Void>> callables = new ArrayList<>();
		for (int i=0; i<POOLSIZE; i++) {
			Worker w = new Worker("add","name"+i);
			callables.add(w);
		}
		for (int i=0; i<POOLSIZE; i++) {
			Worker w = new Worker("update","name"+i);
			callables.add(w);
		}
		try {
			pool.invokeAll(callables);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		map.forEach((key,value)->System.out.println("Key=" + key + ", value=" + value));
		
		pool.shutdown(); //You need to shut down the thread pool when you're done with it or the process will hang around...
		System.out.println("Finished.");
	}
	
	
	private static class Worker implements Callable<Void> {
		
		private String action;
		private String name;
		
		Worker(String action, String name) {
			this.action = action;
			this.name = name;
		}

		@Override
		public Void call() {
			if (action.equalsIgnoreCase("add")) {
				map.putIfAbsent(name, name);
			} else if (action.equalsIgnoreCase("update")) {
				String name = ((String) map.get(this.name));
				map.replace(this.name,name, name+System.currentTimeMillis());
			}
			return null;
		}
		
	}
	
	
}