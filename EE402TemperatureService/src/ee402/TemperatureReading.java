/*
 * Author: Bob Hickson
 * This is an object (Java Bean) that contains information on temperature readings
 * It is serializable so it can be transmitted over a socket
 * 
 * */

package ee402;

import java.io.*;	
import java.util.Calendar;
import java.util.Date;

public class TemperatureReading implements Serializable, Comparable<TemperatureReading> {

	/*
	 * 
	 * temperature, 
	 * date and time of sample, 
	 * current sample number 
	 * */

	private static final long serialVersionUID = 777318411509579412L;
	private Calendar calendar;
	private double theTemperature;
	private Date timeOfSample;
	private long sampleNumber;
	
	public TemperatureReading() {}
	
	public TemperatureReading(double t, Date d) {
		
		this.calendar = Calendar.getInstance();
		this.theTemperature = t;
		this.timeOfSample = d;
		this.sampleNumber = 0;
		
	}
	
	public TemperatureReading(double t, Date d, long s) {
		
		this.calendar = Calendar.getInstance();
		this.theTemperature = t;
		this.timeOfSample = d;
		this.sampleNumber = s;
		
	}
	
	public int compareTo(TemperatureReading T) {
		
		int retval = 0;		
		retval = (int) (this.getTheTemperature() - T.getTheTemperature()); 
		return retval;
		
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public double getTheTemperature() {
		return theTemperature;
	}

	public void setTheTemperature(double theTemperature) {
		this.theTemperature = theTemperature;
	}

	public Date getTimeOfSample() {
		return timeOfSample;
	}

	public void setTimeOfSample(Date timeOfSample) {
		this.timeOfSample = timeOfSample;
	}

	public long getSampleNumber() {
		return sampleNumber;
	}

	public void setSampleNumber(long sampleNumber) {
		this.sampleNumber = sampleNumber;
	}
	
}
