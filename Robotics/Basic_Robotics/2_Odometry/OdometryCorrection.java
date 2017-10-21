
package ev3Odometer;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;



public class OdometryCorrection extends Thread {
	
	// constants
	private static final long CORRECTION_PERIOD = 10;
	private static final int BLACK = 300;
	private static final int WOOD = 400;
	
	final TextLCD t = LocalEV3.get().getTextLCD();

	// sensor requirements
	private Odometer odometer;
	private SensorModes ColorSensor;
	private SampleProvider colorSampler;
	
	// Array to store samples
	private float[] color;
	private float currColor;
	
	// counter of position in path
	private int path;

	public OdometryCorrection(Odometer odometer, EV3ColorSensor ColorSensor, SampleProvider colorSampler) {
		this.odometer = odometer;
		this.ColorSensor = ColorSensor;
		this.colorSampler = colorSampler;
		this.color = new float[colorSampler.sampleSize()];
		this.path = 0;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		// multiple of 30 at line crossing
		double coeff;
		int line = 0;
		boolean corrected = false;
 
		while (true) {
			correctionStart = System.currentTimeMillis();
			
			// get current color and scale it
			colorSampler.fetchSample(color, 0);
			currColor = color[0] * 1000;
			
			// only take action if its a new line that has been encountered
			//if (past coefficient * 30 + 15 )
			if(currColor < BLACK && !corrected) {
				
				Sound.buzz();
								
				// vertical section
				if (path % 2 == 0) {
					// calculate the approximative multiple of 30
					coeff = Math.round((odometer.getY() - Lab2.OFFSET) / 30.48);
					odometer.setY((coeff * 30.48) + Lab2.OFFSET);
				}
				// horizontal section
				else {
					coeff = Math.round((odometer.getX() - Lab2.OFFSET) / 30.48);
					odometer.setX((coeff * 30.48) + Lab2.OFFSET);
				}
				
				t.drawString("COEFF :   "+ coeff, 0, 3);
				t.drawString("NOW AT:   "+((coeff * 30.48) + Lab2.OFFSET), 0, 4);
				t.drawString("PATH :   "+ path, 0, 5);
				t.drawString("X : "+ (int)odometer.getX() + "   Y : " + (int)odometer.getY(), 0, 6);
				
				corrected = true;
				
				// we pass to next path after 3 lines
				line++;
				if (line % 3 == 0) path++;
			} else {
				corrected = false;
			}

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}
