/*
 * Author: Bob Hickson
 * This gets the temperature from the specified AIN pin on the BBB
 * This class is based on the BasicLEDExample.java class provided 
 * in lectures
 * 
 * */

package ee402;

import java.io.*;

public class TemperatureService {
	
	private String PIN_PATH;
	private double theTemperature;
	private String command;
	private BufferedWriter bufferedWriter;
	
	public TemperatureService() {
		
		this.PIN_PATH = "/sys/bus/iio/devices/iio:device0/in_voltage";
		
	}
	
	public String readTemperature(String s) {
		
		this.command = s;
		String retval = "";
		int par = Integer.parseInt(this.command);
		int adc = getADCValue(par);
		double temp = getTemperature(adc);
		this.theTemperature = temp;
		retval = "Temperature is " + this.theTemperature + " degrees Celcius";
		return retval;
		
	}
	
	public double getTemperature(int n) {
		
	   double cur_voltage = n * (1.80 / 4096.0);
	   double diff_degreesC = (cur_voltage - 0.75) / 0.01;
	   return (25.0 + diff_degreesC);
		
	}

	public int getADCValue(int n) {
		
		int retval = n;
		
		try {
		
			this.bufferedWriter = new BufferedWriter (new FileWriter (PIN_PATH + n + "_raw"));
			this.bufferedWriter.write("" + n);
			//this.bufferedWriter.flush();
			//this.bufferedWriter.close();
		
		}
		
		catch (Exception e) { 
			
			pl(e.getMessage());
			e.printStackTrace();
			
		} 
		
		finally {
			
		    if (this.bufferedWriter != null) {
		    
		    	try { 
		    		
		    		this.bufferedWriter.flush();
		    		this.bufferedWriter.close(); 
		    		
		    	}
		    	
		    	catch (IOException exp) {
		    		
		    		pl(exp.getMessage());
					exp.printStackTrace();
		    		
		    	}
		    	
		    }
		    
		    
		}
		
		return retval;
		
	}

	public String getPIN_PATH() {
		return PIN_PATH;
	}

	public void setPIN_PATH(String pIN_PATH) {
		PIN_PATH = pIN_PATH;
	}

	public double getTheTemperature() {
		return theTemperature;
	}

	public void setTheTemperature(double theTemperature) {
		this.theTemperature = theTemperature;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	private void pl(String s) { System.out.println(s); }
	
}
