// Lab2.java

package ev3Odometer;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

public class Lab2 {
	
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	public static final EV3ColorSensor ColorSensor = new EV3ColorSensor(LocalEV3.get().getPort("S1"));
	public static SampleProvider colorSampler = ColorSensor.getMode("Red");
	
	// Constants
	public static final double TRACK = 15.9;								
	public static final double OFFSET = 12.0;								// distance from middle of tires to sensor
	public static final double WHEEL_RADIUS = 2.1;
	public static final double RAD_CIRC = 2 * Math.PI;						// 360 degrees in rads
	public static final double RAD_TO_DEG = 180 / Math.PI;					
	public static final double WHEEL_HALF_CIRC = WHEEL_RADIUS * Math.PI;	// half wheel circumference

	
	
	public static void main(String[] args) {
		int buttonChoice;
		
		final TextLCD t = LocalEV3.get().getTextLCD();
		Odometer odometer = new Odometer(leftMotor,rightMotor);
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer,t);
		OdometryCorrection odometryCorrection = new OdometryCorrection(odometer, ColorSensor, colorSampler);

		do {
			// clear the display
			t.clear();

			// ask the user whether the motors should drive in a square or float
			t.drawString("< Left | Right >", 0, 0);
			t.drawString("       |        ", 0, 1);
			t.drawString(" Float | Drive  ", 0, 2);
			t.drawString("motors | in a   ", 0, 3);
			t.drawString("       | square ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {
			leftMotor.forward();
			leftMotor.flt();
			rightMotor.forward();
			rightMotor.flt();
			
			odometer.start();
			odometryDisplay.start();
			
		} else {
			// start the odometer, the odometry display and (possibly) the
			// odometry correction
			
			odometer.start();
			odometryDisplay.start();
			odometryCorrection.start();

			// spawn a new Thread to avoid SquareDriver.drive() from blocking
			(new Thread() {
				public void run() {
					SquareDriver.drive(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK);
				}
			}).start();
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}