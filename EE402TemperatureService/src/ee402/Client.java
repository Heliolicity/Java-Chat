/* The Client Class - Written by Derek Molloy for the EE402 Module
 * See: ee402.eeng.dcu.ie
 * 
 * Some modifications by: Bob Hickson
 * 
 */

package ee402;

import java.net.*;
import java.io.*;

public class Client {
	
	private static int portNumber = 5050;
    private Socket socket = null;
    private ObjectOutputStream os = null;
    private ObjectInputStream is = null;
    
    /*Variables added by Bob Hickson*/
    //Incremental counter of the number of readings taken
    private long lngNumberOfReadings;
    //Object containing details on the temperature
    private TemperatureReading theReading;
    //Messages received from the server
    private String serverCommunication;

	// the constructor expects the IP address of the server - the port is fixed
    public Client(String serverIP) {
    	if (!connectToServer(serverIP)) {
    		System.out.println("XX. Failed to open socket connection to: " + serverIP);
    		this.serverCommunication = "XX. Failed to open socket connection to: " + serverIP;
    	}
    }

    private boolean connectToServer(String serverIP) {
    	try { // open a new socket to the server 
    		this.socket = new Socket(serverIP,portNumber);
    		this.os = new ObjectOutputStream(this.socket.getOutputStream());
    		this.is = new ObjectInputStream(this.socket.getInputStream());
    		System.out.println("00. -> Connected to Server:" + this.socket.getInetAddress() 
    				+ " on port: " + this.socket.getPort());
    		System.out.println("    -> from local address: " + this.socket.getLocalAddress() 
    				+ " and port: " + this.socket.getLocalPort());
    		
    		this.serverCommunication += "00. -> Connected to Server:" + this.socket.getInetAddress() 
    				+ " on port: " + this.socket.getPort() + "\n";
    		this.serverCommunication += "    -> from local address: " + this.socket.getLocalAddress() 
    				+ " and port: " + this.socket.getLocalPort() + "\n";
    		
    	} 
        catch (Exception e) {
        	System.out.println("XX. Failed to Connect to the Server at port: " + portNumber);
        	System.out.println("    Exception: " + e.toString());
        	this.serverCommunication += "XX. Failed to Connect to the Server at port: " + portNumber + "\n";
        	this.serverCommunication += "    Exception: " + e.toString() + "\n";
        	return false;
        }
		return true;
    }

    public void getDate() {
    	String theDateCommand = "GetDate", theDateAndTime;
    	System.out.println("01. -> Sending Command (" + theDateCommand + ") to the server...");
    	this.serverCommunication += "01. -> Sending Command (" + theDateCommand + ") to the server..." + "\n";
    	this.send(theDateCommand);
    	try{
    		theDateAndTime = (String) receive();
    		System.out.println("05. <- The Server responded with: ");
    		System.out.println("    <- " + theDateAndTime);
    		
    		this.serverCommunication += "05. <- The Server responded with: " + "\n";
    		this.serverCommunication += "    <- " + theDateAndTime + "\n";
    		
    	}
    	catch (Exception e){
    		System.out.println("XX. There was an invalid object sent back from the server");
    		this.serverCommunication += "XX. There was an invalid object sent back from the server" + "\n";
    	}
    	System.out.println("06. -- Disconnected from Server.");
    	this.serverCommunication += "06. -- Disconnected from Server." + "\n";
    }
    
    /*Method added by Bob Hickson*/
    public void getTemperature() {
    	
    	String theTemperatureCommand = "GetTemperature";
    	//TemperatureReading theReading;
    	pl("01. -> Sending Command (" + theTemperatureCommand + ") to the server...");
    	this.serverCommunication += "01. -> Sending Command (" + theTemperatureCommand + ") to the server..." + "\n";
    	this.send(theTemperatureCommand);
    	
    	try{
    		
    		//Receive a TemperatureReading object from the server
    		this.theReading = (TemperatureReading) receive();
    		pl("05. <- The Server responded with: ");
    		this.serverCommunication = "05. <- The Server responded with: ";
    		
    		//Increase the number of readings taken by one
    		lngNumberOfReadings ++;
    		//Set this number on the TemperatureReading
    		theReading.setSampleNumber(lngNumberOfReadings);
    		//Display the details of the reading to the console
    		pl("    <- " + theReading.getSampleNumber());
    		pl("    <- " + theReading.getTimeOfSample());
    		pl("    <- " + theReading.getTheTemperature());
    		this.serverCommunication += "    <- " + theReading.getSampleNumber() + "\n";
    		this.serverCommunication += "    <- " + theReading.getTimeOfSample() + "\n";
    		this.serverCommunication += "    <- " + theReading.getTheTemperature() + "\n";
    		
    	}
    	catch (Exception e){
    		
    		pl("XX. There was an invalid object sent back from the server");
    		this.serverCommunication += "XX. There was an invalid object sent back from the server" + "\n";
    		
    	}
    	
    	pl("06. -- Disconnected from Server.");
    	this.serverCommunication += "06. -- Disconnected from Server." + "\n";
    	
    }
	
    // method to send a generic object.
    private void send(Object o) {
		try {
		    System.out.println("02. -> Sending an object...");
		    this.serverCommunication += "02. -> Sending an object..." + "\n";
		    os.writeObject(o);
		    os.flush();
		} 
	    catch (Exception e) {
		    System.out.println("XX. Exception Occurred on Sending:" +  e.toString());
		    this.serverCommunication += "XX. Exception Occurred on Sending:" +  e.toString() + "\n";
		}
    }

    // method to receive a generic object.
    private Object receive() 
    {
		Object o = null;
		try {
			System.out.println("03. -- About to receive an object...");
			this.serverCommunication += "03. -- About to receive an object..." + "\n";
		    o = is.readObject();
		    System.out.println("04. <- Object received...");
		    this.serverCommunication += "04. <- Object received..." + "\n";
		} 
	    catch (Exception e) {
		    System.out.println("XX. Exception Occurred on Receiving:" + e.toString());
		    this.serverCommunication += "XX. Exception Occurred on Receiving:" + e.toString() + "\n"; 
		}
		return o;
    }

    public TemperatureReading getTheReading() {
		return theReading;
	}

	public void setTheReading(TemperatureReading theReading) {
		this.theReading = theReading;
	}
    
    public String getServerCommunication() {
		return serverCommunication;
	}

	public void setServerCommunication(String serverCommunication) {
		this.serverCommunication = serverCommunication;
	}

	/*Method added by Bob Hickson - simplify means to print data to console*/
	private static void pl(String s) { System.out.println(s); }
    
}