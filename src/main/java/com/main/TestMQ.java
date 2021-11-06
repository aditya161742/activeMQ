package com.main;

public class TestMQ {
	
	public static void main(String[] args) {
       // System.out.println("hello there");
		Producer producer = new Producer();
        Consumer consumer = new Consumer();
        
       
        
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();
        
        Thread producerThread = new Thread(producer);
        producerThread.start();
        
        
    }
}
