package com.main;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;

public class Consumer implements Runnable{
	
	
	protected RedeliveryPolicy getRedeliveryPolicy() {
		   RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
		   redeliveryPolicy.setInitialRedeliveryDelay(0);
		   redeliveryPolicy.setRedeliveryDelay(3000);
		   redeliveryPolicy.setMaximumRedeliveries(3);
		   redeliveryPolicy.setBackOffMultiplier((short) 2);
		   redeliveryPolicy.setUseExponentialBackOff(true);
		   return redeliveryPolicy;
	}
	
	//@Override
    public void run() {
    	
    	System.out.println("Consumer started");
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            factory.setRedeliveryPolicy(getRedeliveryPolicy());
            //Create Connection
            Connection connection = factory.createConnection();

            // Start the connection
            connection.start();

            // Create Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //Create queue
            Destination queue = session.createQueue("New Queue");

            MessageConsumer consumer = session.createConsumer(queue);

            
            ///connection.start();
            
            //while (true) {
            	
            Message message = consumer.receive(1000);
            	
            if (message instanceof TextMessage) {
            	
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                System.out.println("Consumer Received d: " + text);
               // message.acknowledge();
            }
            else{
                System.out.println("Queue Empty"); 
                //connection.stop();
                //break;
            }
            //}
            session.close();
            connection.close();
        }
        catch (Exception ex) {
            System.out.println("Exception Occured");
            System.out.println(ex.getMessage());
        }
    }
}
