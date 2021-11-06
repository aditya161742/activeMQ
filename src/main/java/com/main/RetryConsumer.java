package com.main;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;

/**
 * Created by ishara on 6/12/14.
 */
public class RetryConsumer implements TransportListener
{
    private boolean connected = false;
    private Connection connection = null;
    private MessageConsumer consumer = null;
    private Session session = null;
    private int RETRY_DELAY = 1000;

    public void run() throws Exception
    {

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory( "failover:(tcp://localhost:61616)?timeout=1000" );
        factory.setTransportListener( this );
        connection = factory.createConnection();
        while( session == null )
        {
            try
            {
                session = connection.createSession( false, Session.AUTO_ACKNOWLEDGE );
            }
            catch( JMSException e )
            {
                e.printStackTrace();
            }
            if( session == null )
            {
                System.out.println( "Unable to create ActiveMQ Session" );
                Thread.sleep( RETRY_DELAY );
            }
        }
        connection.start();
        Queue queue = session.createQueue("New Queue");
        consumer = session.createConsumer( queue );
        MessageListener listener = new MessageListener()
        {
            public void onMessage( Message message )
            {
                //System.out.println( "Message Got" );
            	if (message instanceof TextMessage) {
                	
            		try {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    throw InterruptedException();
                    System.out.println("Consumer retry Received: " + text);
            		}
            		catch(Exception e){
            			System.out.println(e.getMessage());
            		}
                   // message.acknowledge();
                }
                else{
                    System.out.println("Queue Empty"); 
                    //connection.stop();
                    //break;
                }
            }
        };
        consumer.setMessageListener( listener );
        // send messages
        while( true )
        {
            if( !connected )
            {
                System.out.println( "Transport Interrupted or IOException" );
            }
            Thread.sleep( RETRY_DELAY );
        }
        
    }

    public void close() throws JMSException
    {
        if( connection != null )
        {
            connection.close();
        }
        if( consumer != null )
        {
            consumer.close();
        }
        if( session != null )
        {
            session.close();
        }
    }

    public static void main( String[] args ) throws Exception
    {
        RetryConsumer producer = new RetryConsumer();
        producer.run();
    }

    public void transportResumed()
    {
        connected = true;
    }

    public void transportInterupted()
    {
        connected = false;
    }

    public void onException( IOException error )
    {
        connected = false;
    }

    public void onCommand( Object command )
    {
        //System.out.println( "Command - " + command );
    }
}