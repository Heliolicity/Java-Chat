/*
 * Author: Bob Hickson
 * This class functions as the threaded client application.
 * It consists of the GUI and also handles the client object.
 * 
 * */

package ee402;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager.*;
import javax.swing.JTabbedPane;

import java.awt.*;
import java.awt.event.*;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.io.*;

import java.util.Random;

@SuppressWarnings("serial")
public class ClientApplication 
	extends JFrame  
	implements ActionListener, 
		WindowListener, 
		Runnable {
	
	
	//It should have a pop-up dialog that allows you to set the server IP address and port number.
	
	//The GUI should provide a graph display of historical readings (e.g., the last 10 readings) in scrolling graphical form. Do not use 3rd party graphing APIs or source code.
	
	//The graphical display should illustrate a moving average temperature and a minimum and maximum temperature.
	
	//The GUI should request the temperature at a time step that is defined by the user. The request should be automatically sent when the time elapses
	
	private String ipAddress;
	private long portNumber;
	private boolean running;
	private Thread thread;
	private Vector<TemperatureReading> theReadings;
	private long interval;
	private Client theClient;
	private long numberOfReadings;
	private TemperatureReading theReading;
	private double minTemp;
	private double maxTemp;
	private double avgTemp;
	
	private JTextArea txtArea;
	private JButton btnIPAddress;
	private JButton btnPortNumber;
	private JButton btnInterval;
	private JButton btnStart;
	private JButton btnStop;
	private JTextField txtIPAddress;
	private JTextField txtPortNumber;
	private JTextField txtInterval;
	private JLabel lblIPAddress;
	private JLabel lblPortNumber;
	private JLabel lblInterval;
	private JLabel lblBlank;
	private JPanel pnlConfigIP;
	private JPanel pnlConfigPort;
	private JPanel pnlConfigInterval;
	private JPanel pnlConfig;
	private JPanel pnlMain;
	private JPanel pnlDetails;
	private JPanel pnlRand;
	private JPanel pnlDisplay;
	private JTextField txtAvgTemp;
	private JTextField txtMinTemp;
	private JTextField txtMaxTemp;
	private JLabel lblAvgTemp;
	private JLabel lblMinTemp;
	private JLabel lblMaxTemp;
	private JRadioButton rdbtnTemp;
	private JRadioButton rdbtnRand;
	private ButtonGroup grpButtons;
	
	private JSplitPane splitPane;
	private JPanel pnlLeft;
	private JPanel pnlRight;
	private JScrollPane leftPane;
	private JScrollPane rightPane;
	private Dimension min;
	private Dimension max;
	
	private JMenuBar menuBar;
	private JMenu optMenu;
	private JMenuItem expAction;
	private JMenuItem autAction;
	private JMenuItem quitAction;
	
	private JTabbedPane tabbedPane;
	
	private GraphicalDisplay theDisplay;
	
	private File outputFile;
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;
	
	private Robot robot;
	private Runtime runtime;
	private int tempType;
	
	public ClientApplication() {
		
		super("Client Application");
		this.setPreferredSize(new Dimension(1000, 900));
		
		/* The GUI will consist of a Split Pane -
		 * 		Left Hand Side will contain textboxes and buttons for determining
		 * 			IP address of the server, port number, the interval during which to take readings
		 * 		Right Hand Side will contain a tabbed pane
		 * 			The first tab will contain a Canvas object that displays the last ten readings taken
		 * 			The second tab will contain three textboxes that display the average temp, 
		 * 				the minimum temperature yet recorded and the maximum temperature yet recorded
		 * 
		 * The GUI will also have an Option menu where the two (hopefully!) interesting additional features
		 * have been included
		 * 		Export - this will export all readings taken so far to a text file located on the local machine
		 * 		Auto - this will use the Java Robot API to override user input, open an instance of Internet Explorer
		 * 			and type in the address of the BeagleBone.  This is to ensure the user that the device is connected 
		 */
		
		try {
            
			//Check to see if the "Nimbus" Look And Feel is present and use this if it is.
			
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
        }
        
        catch (UnsupportedLookAndFeelException ex) { ex.printStackTrace(); } 
        catch (IllegalAccessException ex) { ex.printStackTrace(); } 
        catch (InstantiationException ex) { ex.printStackTrace(); } 
        catch (ClassNotFoundException ex) { ex.printStackTrace(); }
		
		this.min = new Dimension(100, 50);
		this.max = new Dimension(630, 800);
		
		this.tempType = 0;
		
		//Configure GUI objects
		
		this.btnIPAddress = new JButton("Set IP Address");
		this.btnIPAddress.addActionListener(this);
        this.btnPortNumber = new JButton("Set Port Number");
        this.btnPortNumber.addActionListener(this);
        this.btnInterval = new JButton("Set Time Interval");
        this.btnInterval.addActionListener(this);
        
        this.lblIPAddress = new JLabel();
        this.lblIPAddress.setText("IP Address:");
        this.lblPortNumber = new JLabel();
        this.lblPortNumber.setText("Port Number:");
        this.lblInterval = new JLabel();
        this.lblInterval.setText("Time Interval:");
		
        this.txtIPAddress = new JTextField(10);
        this.txtIPAddress.setText("");
        this.txtPortNumber = new JTextField(10);
        this.txtPortNumber.setText("");
        this.txtInterval = new JTextField(10);
        this.txtInterval.setText("");
		
		this.pnlLeft = new JPanel();
		this.pnlLeft.setLayout(new BoxLayout(this.pnlLeft, BoxLayout.Y_AXIS));
		this.pnlLeft.setMinimumSize(this.min);
		this.pnlLeft.setBorder(new TitledBorder("Configuration"));
			
		this.pnlConfigIP = new JPanel();
        this.pnlConfigIP.setLayout(new FlowLayout());
        this.pnlConfigIP.add(this.lblIPAddress);
        this.pnlConfigIP.add(this.txtIPAddress);
        this.pnlConfigIP.add(this.btnIPAddress);
		
        this.pnlLeft.add(this.pnlConfigIP);
        
        this.pnlConfigPort = new JPanel();
        this.pnlConfigPort.setLayout(new FlowLayout());
        this.pnlConfigPort.add(this.lblPortNumber);
        this.pnlConfigPort.add(this.txtPortNumber);
        this.pnlConfigPort.add(this.btnPortNumber);
        
        this.pnlLeft.add(this.pnlConfigPort);
        
        this.pnlConfigInterval = new JPanel();
        this.pnlConfigInterval.setLayout(new FlowLayout());
        this.pnlConfigInterval.add(this.lblInterval);
        this.pnlConfigInterval.add(this.txtInterval);
        this.pnlConfigInterval.add(this.btnInterval);
        
        this.pnlLeft.add(this.pnlConfigInterval);
		
        this.pnlMain = new JPanel();
        this.pnlMain.setLayout(new BoxLayout(this.pnlMain, BoxLayout.Y_AXIS));
        this.pnlMain.setBorder(new TitledBorder("Control Panel"));
        
        this.txtArea = new JTextArea(5, 20);        
        this.txtArea.setText("");
        this.txtArea.setSize(5, 20);
        this.txtArea.setAutoscrolls(true);
        
        this.lblBlank = new JLabel();
        this.lblBlank.setSize(60, 60);
        this.lblBlank.setText("");
        
        this.btnStart = new JButton("Start");
        this.btnStart.addActionListener(this);
        
        this.btnStop = new JButton("Stop");
        this.btnStop.addActionListener(this);
        
        this.pnlConfig = new JPanel();
        this.pnlConfig.setLayout(new BoxLayout(this.pnlConfig, BoxLayout.X_AXIS));
        this.pnlConfig.add(this.btnStart);
        this.pnlConfig.add(this.btnStop);
        
        this.pnlLeft.add(this.pnlConfig);
        this.pnlLeft.add(this.lblBlank);
        this.pnlLeft.add(this.txtArea);
        
        this.pnlDisplay = new JPanel();
        this.pnlDisplay.setLayout(new BoxLayout(this.pnlDisplay, BoxLayout.Y_AXIS));
        
        //This object is an instance of GraphicalDisplay which is a subclass of Canvas
        //This will be used to display the last ten readings on a graph on the Display pane
        //The object needs to be configured with dimensions and with a Vector object consisting of readings
        //(At initialisation this Vector will not contain anything)
        this.theDisplay = new GraphicalDisplay(500, 500, this.theReadings);
        this.theDisplay.setMaximumSize(this.max);
        this.theDisplay.setMinimumSize(this.min);

        this.pnlDisplay.add(this.theDisplay);
                
        this.lblAvgTemp = new JLabel();
        this.lblAvgTemp.setMinimumSize(this.min);
        this.lblAvgTemp.setText("Average Temperature:");
        
        this.txtAvgTemp = new JTextField(50);
        this.txtAvgTemp.setText("");
        
        this.lblMinTemp = new JLabel();
        this.lblMinTemp.setMinimumSize(this.min);
        this.lblMinTemp.setText("Minimum Temperature:");
        
        this.txtMinTemp = new JTextField(50);
        this.txtMinTemp.setText("");
        
        this.lblMaxTemp = new JLabel();
        this.lblMaxTemp.setText("Maximum Temperature:");
        
        this.txtMaxTemp = new JTextField(50);
        this.txtMaxTemp.setText("");
                
        this.pnlRight = new JPanel();
        this.pnlRight.setMaximumSize(this.max);
		this.pnlRight.setLayout(new BoxLayout(this.pnlRight, BoxLayout.Y_AXIS));
		this.pnlRight.setBorder(new TitledBorder("Temperature Sensor"));
		
		this.pnlDetails = new JPanel();
		this.pnlDetails.setMaximumSize(this.max);
		this.pnlDetails.setLayout(new BoxLayout(this.pnlDetails, BoxLayout.Y_AXIS));
		this.pnlDetails.add(this.lblAvgTemp);
        this.pnlDetails.add(this.txtAvgTemp);
        this.pnlDetails.add(this.lblMinTemp);
        this.pnlDetails.add(this.txtMinTemp);
        this.pnlDetails.add(this.lblMaxTemp);
        this.pnlDetails.add(this.txtMaxTemp);
		
        this.pnlRand = new JPanel();
        this.pnlRand.setLayout(new BoxLayout(this.pnlRand, BoxLayout.X_AXIS));
        
        this.rdbtnTemp = new JRadioButton("Actual Temperature Reading");
        this.rdbtnTemp.addActionListener(this);
        this.rdbtnRand = new JRadioButton("Simulated Temperature Reading");
        this.rdbtnRand.addActionListener(this);
        
        this.grpButtons = new ButtonGroup();
        this.grpButtons.add(this.rdbtnTemp);
        this.grpButtons.add(this.rdbtnRand);
        
        this.pnlRand.add(this.rdbtnTemp);
        this.pnlRand.add(this.rdbtnRand);
        
        this.pnlDisplay.add(this.pnlRand);
        
		this.tabbedPane = new JTabbedPane();
		//this.tabbedPane.addTab("Display", this.theDisplay);
		this.tabbedPane.addTab("Display", this.pnlDisplay);
		this.tabbedPane.addTab("Details", this.pnlDetails);
		
		this.pnlRight.add(this.tabbedPane);
        
		this.leftPane = new JScrollPane(this.pnlLeft);
		this.rightPane = new JScrollPane(this.pnlRight);
		
		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.leftPane, this.rightPane);
		this.splitPane.setOneTouchExpandable(true);
		this.splitPane.setDividerLocation(380);
		
		this.getContentPane().add(this.splitPane);
		
		this.addWindowListener(this);        
        
        this.menuBar = new JMenuBar();
        
        setJMenuBar(this.menuBar);
        
        this.optMenu = new JMenu("Options");
        this.expAction = new JMenuItem("Export"); //-- Export the readings to a file
        this.autAction = new JMenuItem("Auto");	//-- Use the Robot to open IE and navigate to the BB
        this.quitAction = new JMenuItem("Quit");	//-- Quit
        
        this.menuBar.add(this.optMenu);
        this.optMenu.add(this.expAction);
        this.optMenu.add(this.autAction);
        this.optMenu.addSeparator();
        this.optMenu.add(this.quitAction);
        
        this.expAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                exportDetails();
            }
        });
        
        this.autAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                checkBBBStatus();
            }
        });
        
        this.quitAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	System.exit(0);
            }
        });
        
		this.pack();
        this.setVisible(true);	
        
        //Set up defaults -- user can just click the Start button without configurint
        this.ipAddress = "192.168.7.2";
        this.portNumber = 5050;
        this.interval = 5000; //-- Take a reading every five seconds as a default
        
        this.txtIPAddress.setText(this.ipAddress);
        this.txtPortNumber.setText("" + this.portNumber);
        this.txtInterval.setText("" + this.interval);
        
        this.txtIPAddress.setEditable(false);
        this.txtPortNumber.setEditable(false);
        this.txtInterval.setEditable(false);
        this.txtArea.setEditable(false);
        
        //Set up the Thread for running
        this.running = true;
        this.thread = new Thread(this);
        
	}
	
	public void actionPerformed(ActionEvent e) {
		
		String inp;
		
		if (e.getSource().equals(this.btnIPAddress)) {
			
			this.ipAddress = JOptionPane.showInputDialog(this, "Configure IP Address", "IP Address", JOptionPane.QUESTION_MESSAGE);
			this.txtIPAddress.setEditable(true);
			this.txtIPAddress.setText(this.ipAddress);
			this.txtIPAddress.setEditable(false);
			appendTextAreaValue("IP Address:\t" + this.ipAddress);
			
		} 
		
		else if (e.getSource().equals(this.btnPortNumber)) {
			
			inp = JOptionPane.showInputDialog(this, "Configure Port Number", "Port Number", JOptionPane.QUESTION_MESSAGE);
			
			try {
				
				this.txtPortNumber.setEditable(true);
				this.portNumber = Long.parseLong(inp);
				this.txtPortNumber.setText(inp);
				this.txtPortNumber.setEditable(false);
				appendTextAreaValue("Port Number:\t" + inp);
			
			} 
			
			catch (NumberFormatException nfe) { setTextAreaValue("The Port Number entered: " + inp + " is invalid"); } 
			
			catch (NullPointerException npe) { setTextAreaValue("The Port Number entered is null"); } 
			
		} 
		
		else if (e.getSource().equals(this.btnInterval)) {
			
			inp = JOptionPane.showInputDialog(this, "Configure Time Interval", "Time Interval", JOptionPane.QUESTION_MESSAGE);
			
			try {
				
				this.txtInterval.setEditable(true);
				this.interval = Long.parseLong(inp);
				this.txtInterval.setText(inp);
				this.txtInterval.setEditable(false);
				appendTextAreaValue("Interval:\t" + inp);
			
			} 
			
			catch (NumberFormatException nfe) { setTextAreaValue("The Interval entered: " + inp + " is invalid"); }
			
			catch (NullPointerException npe) { setTextAreaValue("The Interval entered is null"); } 
			
		} 
		
		else if (e.getSource().equals(this.btnStart)) {
			
			//Has an IP Address been enetered?
			if (! (this.ipAddress == null)) { 
			
				//Are we sure it's not blank?
				if (this.ipAddress.trim() != "") {
			
					try {
						
						//Start taking readings!
						this.running = true;
						this.thread.start();
						this.btnStart.setEnabled(false);
					
					}
					
					catch (IllegalThreadStateException itse) { setTextAreaValue("Thread is in an illegal state"); }
					
				} 
				
				else { setTextAreaValue("The IP Address is blank"); }
				
			} 
			
			else { setTextAreaValue("The IP Address is null"); }
			
		} 
		
		else if (e.getSource().equals(this.rdbtnTemp)) { this.tempType = 1; }
		
		else if (e.getSource().equals(this.rdbtnRand)) { this.tempType = 2; }
		
		//Stop taking readings
		else if (e.getSource().equals(this.btnStop)) { this.running = false; }
		
	}
	
	public void run() {
		
		//The client object - this will be used to communicate with the server
		this.theClient = new Client(this.ipAddress);
		//Create a Vector of TemperatureReading objects
		this.theReadings = new Vector<TemperatureReading>();
		this.minTemp = 0.0;
		this.maxTemp = 0.0;
		double randTemp = 0.0;
		
		while (running) {
		
			try {
		
				//Get the date from the server
				this.theClient.getDate();
				//Get the temperature from the server
            	this.theClient.getTemperature();
            	//Get the TemperatureReading object
            	this.theReading = this.theClient.getTheReading();
            	//Increment the number of readings taken
            	this.numberOfReadings ++;
            	//Set this number on the object
            	this.theReading.setSampleNumber(this.numberOfReadings);
            	
            	/* In the event of there being problems with the TMP36 sensor I 
            	 * included a random number generator to simulate temperatures
            	 * 
            	 */
            	//Check to see if we want actual readings off the BB or simulated readings
            	
            	if (this.tempType <= 1) {
            		
            		//Add this TemperatureReading sequentially to the Vector
            		this.theReadings.add(((int) this.numberOfReadings - 1), this.theReading);
            		
            	}
            	
            	else if (this.tempType == 2) {
            		
            		//Update the temperature to the new random value
            		randTemp = getRandomNumber(-100.0, 100.0);
            		this.theReading.setTheTemperature(randTemp);
            		
            		//Add this TemperatureReading sequentially to the Vector
            		this.theReadings.add(((int) this.numberOfReadings - 1), this.theReading);
            		
            	}
			
            	//Deliver the updated Vetor of readings to the Display on the Display tab
            	this.theDisplay.setTheReadings(this.theReadings);
            	//Get the average temperature taken so far
            	this.avgTemp = this.theDisplay.getAvgTemp();
            	//Get the minimum temperature taken so far
            	this.minTemp = this.theDisplay.getMinTemp();
            	//Get the maximum temperature taken so far
            	this.maxTemp = this.theDisplay.getMaxTemp();
            	//Send these values to the textboxes on the Details tab
            	this.txtAvgTemp.setText("" + this.avgTemp);
            	this.txtMinTemp.setText("" + this.minTemp);
            	this.txtMaxTemp.setText("" + this.maxTemp);
            	
            	//Refresh the Canvas
            	this.theDisplay.repaint();
            	//Refresh the Details
            	this.txtAvgTemp.repaint();
            	this.txtMinTemp.repaint();
            	this.txtMaxTemp.repaint();
            	this.pnlDetails.repaint();
            	
            	//Update the text area with the latest reading
            	appendTextAreaValue("Sample: " + this.theReading.getSampleNumber());
            	appendTextAreaValue("Time: " + this.theReading.getTimeOfSample());
            	appendTextAreaValue("Temperature: " + this.theReading.getTheTemperature());
            	appendTextAreaValue("------");
            	appendTextAreaValue("------");
            	
            	//Wait for the user-specified interval and take another reading
				Thread.sleep(this.interval);
				
			} 
			
			catch (InterruptedException e) { setTextAreaValue("Thread was Interrupted!"); }
			
			catch (NullPointerException npe) { 
				
				setTextAreaValue("Null value - connection may have been reset");
				this.running = false;
				this.btnStart.setEnabled(true);
				return;
				
			}
			
		}
	
	}
	
	public void exportDetails() {
		
		/*
		 * This is the first of the additional features
		 * When triggered this method will use File I/O to export the details 
		 * of all the readings taken so far to a TEMPERATURE_READINGS.txt file
		 * at C:\Users\Public\Documents\
		 * 
		 * */
		
		try {			
			
			this.outputFile = new File("C:\\Users\\Public\\Documents\\TEMPERATURE_READINGS.txt");
			this.fileWriter = new FileWriter(this.outputFile.getAbsoluteFile());
			this.bufferedWriter = new BufferedWriter(this.fileWriter);
			
			TemperatureReading TR;
			String out;
			
			//Make sure the output file exists - if not then create it
			if (! this.outputFile.exists())	this.outputFile.createNewFile();
			
			//Are there readings to export?
			if (! (this.theReadings == null)) { 
			
				//Are we sure there are?
				if (this.theReadings.size() > 0) {
			
					//For each reading
					for (int i = 0; i < this.theReadings.size(); i ++) {
			
						//Export its details
						TR = this.theReadings.elementAt(i);
						out = "Sample: " + TR.getSampleNumber() + "\n";
						this.bufferedWriter.write(out);
						this.bufferedWriter.write("\n");
		            	out = "Time: " + TR.getTimeOfSample() + "\n";
		            	this.bufferedWriter.write("\n");
		            	this.bufferedWriter.write(out);
		            	out = "Temperature: " + TR.getTheTemperature() + "\n";
		            	this.bufferedWriter.write("\n");
		            	this.bufferedWriter.write(out);
		            	out = "------" + "\n";
		            	this.bufferedWriter.write("\n");
		            	this.bufferedWriter.write(out);
		            	out = "------" + "\n";
		            	this.bufferedWriter.write("\n");
		            	this.bufferedWriter.write(out);
		            	
					}
					
					this.bufferedWriter.close();
					
				}
			
			}

		}
		
		catch (Exception ex){ 
			
			appendTextAreaValue("Error writing to file");
			pl(ex.getMessage());
			ex.printStackTrace();
			
		}
		
	}
	
	public void checkBBBStatus() {
		
		/*
		 * This is the second of the additional features
		 * It uses the Java Robot API to override keyboard input - a user will 
		 * not be able to use the keyboard for the duration of this method's running (under 30 secs)
		 * 
		 * It opens an instance of Internet Explorer using the Runtime
		 * It then clears the URL field of IE and types in the IP Address of the BBB
		 * This will allow users to ensure that the device is online
		 * 
		 * */
		
		try {
			
            this.robot = new Robot();
            this.runtime = Runtime.getRuntime();
            
            this.runtime.exec("C:\\Program Files\\Internet Explorer\\iexplore.exe");
            this.robot.delay(2000);
            
            this.robot.keyPress(KeyEvent.VK_F6);
            this.robot.keyPress(KeyEvent.VK_DELETE);
            
            this.robot.keyPress(KeyEvent.VK_1);
            this.robot.delay(1000);
            this.robot.keyPress(KeyEvent.VK_9);
            this.robot.delay(1000);
            this.robot.keyPress(KeyEvent.VK_2);
            this.robot.delay(1000);
            this.robot.keyPress(KeyEvent.VK_PERIOD);
            this.robot.delay(1000);
            this.robot.keyPress(KeyEvent.VK_1);
            this.robot.delay(1000);
            this.robot.keyPress(KeyEvent.VK_6);
            this.robot.delay(1000);
            this.robot.keyPress(KeyEvent.VK_8);
            this.robot.delay(1000);
            this.robot.keyPress(KeyEvent.VK_PERIOD);
            this.robot.delay(1000);
            this.robot.keyPress(KeyEvent.VK_7);
            this.robot.delay(1000);
            this.robot.keyPress(KeyEvent.VK_PERIOD);
            this.robot.delay(1000);
            this.robot.keyPress(KeyEvent.VK_2);
            this.robot.delay(1000);
            this.robot.keyPress(KeyEvent.VK_ENTER);
            
        } 
		
		catch (AWTException e) {
			
			appendTextAreaValue("Error with automating input");
			pl(e.getMessage());
			e.printStackTrace();
			
		}
		
		catch (IOException ex) {
			
			appendTextAreaValue("Error with automating input");
			pl(ex.getMessage());
			ex.printStackTrace();
			
		}
		
	}
	
	private void setTextAreaValue(String s) { this.txtArea.setText(s); }
	
	private void appendTextAreaValue(String s) {
		
		String text = this.txtArea.getText();
		this.txtArea.setText(text + "\n" + s);
		
	}
	
	//If there are problems with the TMP36 use a randomly-generated number for the temperature
	/*Method created by Bob Hickson - create a random double between the specified extremes*/
    public double getRandomNumber(double min, double max) {
		
		Random random = new Random();
		double range = max - min;
		double amendedNumber = random.nextDouble() * range;
		double retval = amendedNumber + min;
		return retval; 
		  
	}
	
	public void windowActivated(WindowEvent arg0) {}
    
	public void windowClosed(WindowEvent arg0) {}
    
	public void windowClosing(WindowEvent arg0) { System.exit(0); }
    
	public void windowDeactivated(WindowEvent arg0) {}
    
	public void windowDeiconified(WindowEvent arg0) {}
    
	public void windowIconified(WindowEvent arg0) {}
    
	public void windowOpened(WindowEvent arg0) {}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public long getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(long portNumber) {
		this.portNumber = portNumber;
	}
	
	/*Simplify means to print data to console*/
	private void pl(String s) { System.out.println(s); }
	
	public static void main(String[] args) {
		
		new ClientApplication();
		
	}
	
}
