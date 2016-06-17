/*
 * Author: Bob Hickson
 * This class is a subclass of Canvas and functions as the 
 * graphical display outputting temperature readings to the GUI.
 * It is called and updated from the ClientApplication class 
 * which refreshes it with new readings (in the form of a Vector of 
 * TemperatureReading objects) which contain details taken from the BBB.
 * 
 * Temperature readings are displayed as ovals drawn on a canvas with a 
 * text accompaniment outlining the temperature in degrees Celcius and 
 * the date and time the reading was taken
 * 
 * */

package ee402;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.*;

@SuppressWarnings("serial")
public class GraphicalDisplay extends Canvas implements MouseListener {

	//Vector of TemperatureReading objects updated by the ClientApplication
	private Vector<TemperatureReading> theReadings;
	private int width;
	private int height;
	private TemperatureReading temperatureReading;
	private int radius = 5;
	private int distance = 50;
	private int xOrd = this.distance;
	private int yOrd = 0;
	private double minTemp;
	private double maxTemp;
	private double avgTemp;
	
	public GraphicalDisplay() {}
	
	public GraphicalDisplay(int w, int h, Vector<TemperatureReading> v) {
		
		this.theReadings = v;
		this.width = w;
		this.height = h;
		this.setBackground(Color.white);
		
		this.setSize(this.width, this.height);
        this.addMouseListener(this);
        this.repaint();
		
	}
	
	public GraphicalDisplay(int w, int h) {
		
		this.theReadings = new Vector<TemperatureReading>();
		this.width = w;
		this.height = h;
		
		this.setSize(this.width, this.height);
        this.addMouseListener(this);
        this.repaint();
		
	}
	
	public void paint(Graphics g) {
		
		this.xOrd = 0;
		int startPos = 0;
		this.avgTemp = 0;
		this.minTemp = 0.0;
		this.maxTemp = 0.0;
		double runTotal = 0.0;
		DecimalFormat formatter = new DecimalFormat("####0.00");
		
		try {
		
			//Print out a grid to the Canvas
			g.setColor(Color.black);
			g.drawLine(0, 0, 0, this.height);
			g.drawLine(0, this.height, this.width, this.height);
			
			g.setColor(Color.gray);
			
			for (int a = this.height; a >= 0; a -= 10) g.drawLine(0, this.height - a, this.width, this.height - a);
			
			//Are there readings?
			if (this.theReadings.size() > 0) {
			
				//If there are less than 10 readings show them all
				if (this.theReadings.size() <= 10) startPos = 0;
					
				//Otherwise just show the last 10 readings received
				else startPos = this.theReadings.size() - 10;
				
				for (int i = startPos; i < this.theReadings.size(); i ++) {
					
					this.temperatureReading = this.theReadings.elementAt(i);
					
					//Update the average temperature taken
					runTotal += this.temperatureReading.getTheTemperature();
					this.avgTemp = runTotal / this.theReadings.size();
					
					//Check to see if this temperature is a new maximum
					if (this.temperatureReading.getTheTemperature() >= this.maxTemp) this.maxTemp = this.temperatureReading.getTheTemperature();
					
					//Check to see if this temperature is a new minimum
					if (this.temperatureReading.getTheTemperature() <= this.minTemp) this.minTemp = this.temperatureReading.getTheTemperature();
					
					//If it's less than ten then assume it's cold - change the colour of the oval on the output to blue
					if (this.temperatureReading.getTheTemperature() < 10.0) g.setColor(Color.blue);
					else g.setColor(Color.red);
				
					this.yOrd = this.distance + ((int) this.temperatureReading.getTheTemperature());
					
					if (this.yOrd < 0) this.yOrd = 0; 
					
					//Draw an oval using the Y-axis to denote changes in temperature and the X-axis to denote changes in time
					g.fillOval(this.xOrd, this.height - this.yOrd - 5, 2 * this.radius, 2 * this.radius);
					//Include a text reading of the temperature
					g.drawString("T: " + formatter.format(this.temperatureReading.getTheTemperature()) + "C", this.xOrd, this.height - this.yOrd - 5);
					//Include the date/time the reading was taken
					g.drawString("D: " + this.temperatureReading.getTimeOfSample(), this.xOrd, this.height - this.yOrd - 15);
					
					//Increase the distance on the X-axis to account for the next reading - this causes the display to scroll to the right
					this.xOrd += this.distance;
					
				}
				
			}
		
		} catch (NullPointerException ex) {}
		
	}

	public void mouseClicked(MouseEvent e) {}
	
	public void mouseEntered(MouseEvent e) {}
	
	public void mouseExited(MouseEvent e) {}
	
	public void mousePressed(MouseEvent e) {}
	
	public void mouseReleased(MouseEvent e) {}

	public Vector<TemperatureReading> getTheReadings() {
		return theReadings;
	}

	public void setTheReadings(Vector<TemperatureReading> theReadings) {
		this.theReadings = theReadings;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public TemperatureReading getTemperatureReading() {
		return temperatureReading;
	}

	public void setTemperatureReading(TemperatureReading temperatureReading) {
		this.temperatureReading = temperatureReading;
	}

	public double getMinTemp() {
		return minTemp;
	}

	public void setMinTemp(double minTemp) {
		this.minTemp = minTemp;
	}

	public double getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp(double maxTemp) {
		this.maxTemp = maxTemp;
	}

	public double getAvgTemp() {
		return avgTemp;
	}

	public void setAvgTemp(double avgTemp) {
		this.avgTemp = avgTemp;
	}

	//private void pl(String s) { System.out.println(s); }
	
}
	

