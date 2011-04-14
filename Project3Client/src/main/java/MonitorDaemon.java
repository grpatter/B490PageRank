package main.java;

import java.util.LinkedList;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.hyperic.sigar.Sigar;

public class MonitorDaemon implements Runnable {

	private volatile boolean running = false;
	private static Sigar sigar = null;
	private LinkedList<InfoPacket> recordedData = new LinkedList<InfoPacket>();
	// TODO thisneeds to be synchronized
	private static Connection connection = null;

	private String host;
	private String port;
	private String clusterName;
	private String daemonNo;
	private Properties configProps;
	

	public MonitorDaemon(String host, String port, String clusterName,
			String daemonNo) {
		super();
		this.host = host;
		this.port = port;
		this.clusterName = clusterName;
		this.daemonNo = daemonNo;
	}
	
	public MonitorDaemon(Properties configProps){
		  clusterName = configProps.getProperty(MonitorConstants.ConfigProperties.BROKER_CLUSTER);
		  daemonNo = configProps.getProperty(MonitorConstants.ConfigProperties.BROKER_DAEMON_NUM);
		  host = configProps.getProperty(MonitorConstants.ConfigProperties.BROKER_HOST);
		  port = configProps.getProperty(MonitorConstants.ConfigProperties.BROKER_PORT);
		  System.out.println("MonitorDaemon successfully started with configuration: " + host + ":" + port + " on cluster '" + clusterName + "' and daemon Number: " + daemonNo);
		  this.configProps = configProps;
		  this.start();
	}

	public void start() {
		String connect = this.configProps.getProperty(MonitorConstants.ConfigProperties.BROKER_ENABLE);
		System.out.println("Connection config set to " + connect);
		if(!connect.equals("false")){
			
			// Create a ConnectionFactory
			String connectUrl = "failover://tcp://"+host+":"+port;
	        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(connectUrl);
	        connectionFactory.setUseAsyncSend(true);
	
	        // Create a Connection
	        try {
				connection = connectionFactory.createConnection();
				connection.start();
			} catch (JMSException e) {
				System.out.println("FATAL ERROR: ActiveMQ encountered a JMSException. Broker Consumption will fail. Stopping monitor.");
				e.printStackTrace();
				this.setRunning(false);
				this.shutdown();
			}
		}else{
			System.out.println("WARNING: Monitor Thread started without setting up a broker connection. Consuming will fail!");
		}

	}

	public void run() {
		while (running) {
			try {

				if("true".equals(this.configProps.getProperty(MonitorConstants.ConfigProperties.BROKER_ENABLE))){
					try {
						//BEGIN SEND DATA 
						// Create a Session
		                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
						// Create the destination (Topic or Queue)
//		                Destination destination = session.createQueue(clusterName+":"+daemonNo);//TODO use our topic ...

		                Destination destination = session.createQueue("grpatter.test");//TODO use our topic ...
		
		                // Create a MessageConsumer from the Session to the Topic or Queue
		                MessageConsumer consumer = session.createConsumer(destination);
		
		                // Wait for a message
		                Message message = consumer.receive(1000);
		                if(message == null){
		                	System.out.println("***Nothing Received from Broker, skipping.");		                	
		                }else if(message instanceof ObjectMessage){
		                	ObjectMessage obj = (ObjectMessage)message;
		                	InfoPacket ip = (InfoPacket)obj.getObject();
		                	System.out.println("***InfoPacket Received from Host: " + ip.getNetInfo().getDomainName() + "/" + ip.getNetInfo().getHostName());
		                	System.out.println("***" + ip.getReportString() + "***");
		                	
		                }

//		                if (message instanceof TextMessage) {
//		                    TextMessage textMessage = (TextMessage) message;
//		                    String text = textMessage.getText();
//		                    System.out.println("Received TextMessage...wtf do we do now.: " + text);
//		                    
//		                    String delims = "[ ]+";
//		                    String[] tokens = text.split(delims);
//		                    
//		                    //TODO these need reconsidered...where and how to use this message.. pass it?
//		                    Long usedmem = Long.valueOf(tokens[1]);
//		                    Long totalmem = Long.valueOf(tokens[2]);
//		                    
//		                } else {
//		                	Long usedmem = 0L;
//		                    System.out.println("Received something...no idea what it is.: " + message);
//		                }
		
		                // Clean up
		                session.close();
		
		                //END SEND DATA
			        } catch (JMSException e) {
						System.out.println("ERROR: Sending encountered a JMSException. Broker Publishing will fail for this message.");
						e.printStackTrace();
					}
				}
				
				Thread.sleep((int) (MonitorConstants.SYS_MONITOR_INTERVAL));
			} catch (UnsatisfiedLinkError e0) {
				System.out
						.println("FATAL ERROR: Sigar encountered an unsatisfied link. Logging will fail from here forward. Stopping monitoring thread.");
				System.out
						.println("WARNING: Swallowing stack trace !!!");
				// e0.printStackTrace();
				// this.setRunning(false);
			} catch (InterruptedException e) {
				System.out.println("WARNING: Interrupt caught, shutting down monitor...\n");
				this.setRunning(false);
				break;
			}
		}
		this.shutdown();
	}


	public void shutdown() {
		System.out.println("Shutdown in progress.");
		this.setRunning(false);
		sigar.close();
		try {
			connection.close();
		} catch (JMSException e) {
			System.out.println("WARNING: JMSException while closing the connection...\n");
			e.printStackTrace();
		}
		System.out.println("NOTICE: Monitor shutdown complete.\n");
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}	
}
