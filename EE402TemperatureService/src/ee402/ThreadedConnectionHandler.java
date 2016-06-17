/* The Connection Handler Class - Written by Derek Molloy for the EE402 Module
 * See: ee402.eeng.dcu.ie
 * 
 * Some modifications by: Bob Hickson
 * 
 */

package ee402;

import java.net.*;
import java.io.*;
import java.util.Date;

public class ThreadedConnectionHandler extends Thread
{
    private Socket clientSocket = null;				// Client socket object
    private ObjectInputStream is = null;			// Input stream
    private ObjectOutputStream os = null;			// Output stream
    private DateTimeService theDateService;
    
    /*Added by Bob Hickson*/
    private long lngNumberOfReadings; //-- Sequential number of readings
    private TemperatureService theTemperatureService; //-- Class allowing for handling of TMP36 sensor
    
	// The constructor for the connection handler
    public ThreadedConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        //Set up a service object to get the current date and time
        this.theDateService = new DateTimeService();
        //this.lngNumberOfReadings = 0;
        this.theTemperatureService = new TemperatureService();
    }

    // Will eventually be the thread execution method - can't pass the exception back
    public void run() {
         try {
            this.is = new ObjectInputStream(clientSocket.getInputStream());
            this.os = new ObjectOutputStream(clientSocket.getOutputStream());
            while (this.readCommand()) {}
         } 
         catch (IOException e) 
         {
        	System.out.println("XX. There was a problem with the Input/Output Communication:");
            e.printStackTrace();
         }
    }

    // Receive and process incoming string commands from client socket 
    private boolean readCommand() {
        String s = null;
        try {
            s = (String) is.readObject();
        } 
        catch (Exception e){    // catch a general exception
        	this.closeSocket();
            return false;
        }
        System.out.println("01. <- Received a String object from the client (" + s + ").");
        
        // At this point there is a valid String object
        // invoke the appropriate function based on the command 
        if (s.equalsIgnoreCase("GetDate")){ 
            this.getDate(); 
        } else if (s.equalsIgnoreCase("GetTemperature")) {
        	
        	this.getTemperature();
        	
        }
        
        else { 
            this.sendError("Invalid command: " + s); 
        }
        return true;
    }

    // Use our custom DateTimeService Class to get the date and time
    private void getDate() {	// use the date service to get the date
        String currentDateTimeText = theDateService.getDateAndTime();
        this.send(currentDateTimeText);
    }
    
    /*Method added by Bob Hickson - gets the temperature from the server*/
    private void getTemperature() {
    	
    	//Get the date from the DateService class
    	Date theDate = theDateService.getDate(); 
    	
    	//Increment the number of readings
    	lngNumberOfReadings ++;
    	double temp = 0.0;
    	String rep = "";
    	
    	//Assume that the TMP36 is connected to AIN4
    	rep = this.theTemperatureService.readTemperature("4");
    	pl(rep);
    	temp = this.theTemperatureService.getTheTemperature();
    	
    	//All details necessary to send this reading are present so create a new TemperatureReading object
    	TemperatureReading theReading = new TemperatureReading(temp, theDate, lngNumberOfReadings);
    	this.send(theReading);
    	
    }

    // Send a generic object back to the client 
    private void send(Object o) {
        try {
            System.out.println("02. -> Sending (" + o +") to the client.");
            this.os.writeObject(o);
            this.os.flush();
        } 
        catch (Exception e) {
            System.out.println("XX." + e.getStackTrace());
        }
    }
    
    // Send a pre-formatted error message to the client 
    public void sendError(String message) { 
        this.send("Error:" + message);	//remember a String IS-A Object!
    }
    
    // Close the client socket 
    public void closeSocket() { //gracefully close the socket connection
        try {
            this.os.close();
            this.is.close();
            this.clientSocket.close();
        } 
        catch (Exception e) {
            System.out.println("XX. " + e.getStackTrace());
        }
    }
    
    /*Method added by Bob Hickson - simplify printing out to the console*/
    public void pl(String s) { System.out.println(s); }

}