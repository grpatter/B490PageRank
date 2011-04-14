package main.java;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

import org.apache.activemq.ActiveMQConnectionFactory;

public class FrontEnd extends JPanel{
	
	private static MonitorClient monitorClient;
	
	private TimeSeries total;
	private TimeSeries free;
	
	private static long usedmem;
	private static long totalmem;
	private static long cpu;
	
	public FrontEnd(int maxAge) {

		super(new BorderLayout());

		// create two series that automatically discard data more than 30
		// seconds old...
		this.total = new TimeSeries("Total Memory", Millisecond.class);
		this.total.setMaximumItemAge(maxAge);
		this.free = new TimeSeries("Free Memory", Millisecond.class);
		this.free.setMaximumItemAge(maxAge);

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(this.total);
		dataset.addSeries(this.free);

		DateAxis domain = new DateAxis("Time");
		NumberAxis range = new NumberAxis("Memory");

		domain.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		domain.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));
		range.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));

		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesPaint(1, Color.green);
		renderer.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));

		XYPlot plot = new XYPlot(dataset, domain, range, renderer);
		plot.setBackgroundPaint(Color.black);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		domain.setAutoRange(true);
		domain.setLowerMargin(0.0);
		domain.setUpperMargin(0.0);
		domain.setTickLabelsVisible(true);
		range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		JFreeChart chart = new JFreeChart("Memory Usage", new Font("SansSerif", Font.BOLD, 24), plot, true);
		chart.setBackgroundPaint(Color.white);

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4), BorderFactory.createLineBorder(Color.black)));
		add(chartPanel);
	}
	
	private void addTotalObservation(double y) {
		this.total.add(new Millisecond(), y);
	}


	private void addUsedObservation(double y) {
		this.free.add(new Millisecond(), y);
	}
	
	class MemGenerator extends Timer implements ActionListener {

		MemGenerator(int interval) {
			super(interval, null);
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent event) {
			HashMap<String, InfoPacket> ipStore = (HashMap<String, InfoPacket>) monitorClient.getmDaemon().getIpStore().clone();
			monitorClient.getmDaemon().getIpStore().clear();
			if(ipStore != null){
				this.dataAverageUpdater(ipStore);
			}else{
				System.out.println("No data");
			}			
			if(usedmem != 0) {
				addTotalObservation(totalmem);
				addUsedObservation(usedmem);
			}
		}
		
		public void dataAverageUpdater(HashMap<String, InfoPacket> ipStore){
			int daemons = ipStore.keySet().size();
			if(daemons == 0){return;}
			long usedmemAvg = 0;
			long totalmemAvg = 0;
			long cpuAvg = 0;
			
		    for (InfoPacket curIp : ipStore.values()) {
				usedmemAvg += curIp.getMemInfo().getUsed();
				totalmemAvg += curIp.getMemInfo().getTotal();
				cpuAvg += curIp.getCpuPerc().getSys();
		    }
			usedmem = usedmemAvg/daemons;
			totalmem = totalmemAvg/daemons;
			cpu = cpuAvg/daemons;
		}
	}
	
	public static void main(String[] args) throws Exception {   
		boolean running = true;
		
		Properties props = new Properties();
		InputStream inStream = null;
		try{
			  inStream = System.class.getResourceAsStream("/main/resources/"+"config.properties");
			  props.load(inStream);
		}catch(FileNotFoundException e){
			System.out.println("ERROR: Unable to find properties file.");
			e.printStackTrace();
		}catch(IOException e1){
			System.out.println("ERROR: Unable to load properties file.");
			e1.printStackTrace();			
		}finally {
			if( null != inStream ) try { inStream.close(); } catch( IOException e ) { /* .... */ }
		}
		monitorClient = new MonitorClient(props);
		
		JFrame frame = new JFrame("System Monitor (Memory)");
		FrontEnd panel = new FrontEnd(30000);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.setBounds(200, 120, 1000, 500);
		frame.setVisible(true);
		panel.new MemGenerator(1000).start();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				monitorClient.terminateWorker();
				System.exit(0);
			}
		});
		
		while(true){
	        Thread.sleep(500);
	    }
	}

	@Deprecated
	public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }
	
	@Deprecated
	public static class Consumer implements Runnable, ExceptionListener {
        public void run() {
            try {
                // Create a ConnectionFactory
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://129.79.49.181:61616");
            	//ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
                
                // Create a Connection
                Connection connection = connectionFactory.createConnection();
                connection.start();

                connection.setExceptionListener(this);

                // Create a Session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                // Create the destination (Topic or Queue)
                Destination destination = session.createQueue("G17_007.TOPIC");

                // Create a MessageConsumer from the Session to the Topic or Queue
                MessageConsumer consumer = session.createConsumer(destination);

                // Wait for a message
                Message message = consumer.receive(1000);

                if(message == null){
                	System.out.println("Nothing Received from Broker, skipping.");	
                	usedmem = 0;	                	
                }else if(message instanceof ObjectMessage){
                	ObjectMessage obj = (ObjectMessage)message;
                	InfoPacket ip = (InfoPacket)obj.getObject();
                	System.out.println(ip.getReportString() + "***");
//                    usedmem = Long.valueOf(tokens[1]);
//                    totalmem = Long.valueOf(tokens[2]);
                    usedmem = ip.getMemInfo().getUsed();
                    totalmem = ip.getMemInfo().getTotal();
//                    cpu = ip.getCpuPerc().get
                }else{
                	usedmem = 0;
                }
                
                // Clean up
                consumer.close();
                session.close();
                connection.close();
                
            } catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            }
        }

		@Override
		public void onException(JMSException arg0) {
			// TODO Auto-generated method stub			
		}
	}
}
