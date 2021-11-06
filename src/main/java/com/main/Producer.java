package com.main;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.ScheduledMessage;

public class Producer implements Runnable{
	
	
	protected RedeliveryPolicy getRedeliveryPolicy() {
		   RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
		   redeliveryPolicy.setInitialRedeliveryDelay(0);
		   redeliveryPolicy.setRedeliveryDelay(3000);
		   redeliveryPolicy.setMaximumRedeliveries(3);
		   redeliveryPolicy.setBackOffMultiplier((short) 2);
		   redeliveryPolicy.setUseExponentialBackOff(true);
		   return redeliveryPolicy;
	}
	
	  public void run() {
		  
		  System.out.println("Producer started");
		  
		 
		  
	        try { 
	        	
	        	 Thread.sleep(5000);
	        	 System.out.println("after sleep producer called");
	        	 // Create a connection factory.
	            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
	            
	            factory.setRedeliveryPolicy(getRedeliveryPolicy());
	            
	            //Create connection.
	            Connection connection = factory.createConnection();

	            // Start the connection
	            connection.start();
	            System.out.println("after connection call");
	            // Create a session which is non transactional
	            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

	            // Create Destination queue
	            Destination queue = session.createQueue("New Queue");

	            // Create a producer
	            MessageProducer producer = session.createProducer(queue);

	            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
	            
	           for(int u  =0;u<10;u++)
	            {
	            String msg = "Mail send "+u;

	            // insert message
	            TextMessage message = session.createTextMessage(msg);
	            System.out.println("Producer Sent: " + msg);
	            
	            producer.send(message);
	           }
	            
	            session.close();
	            connection.close();
	        }
	        catch (Exception ex) {
	            System.out.println("Exception Occured");
	            System.out.println(ex.getMessage());
	        }
	    }
}
