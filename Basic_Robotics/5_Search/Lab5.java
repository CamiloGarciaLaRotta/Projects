package ev3Search;

import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;

public class Lab5 {

	// Static Resources

	// wheel spacing and radius
	public static final double TRACK = 8.3;
	public static final double WHEEL_RADIUS = 2.08;

	// motor speeds
	public static int MOTOR_LOW = 50; // (deg/sec)
	public static int MOTOR_HIGH = 150; // (deg/sec)

	// motors and sensors
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(
			LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(
			LocalEV3.get().getPort("D"));
	private static final Port usPort = LocalEV3.get().getPort("S1");
	private static final Port colorPort = LocalEV3.get().getPort("S2");

	@SuppressWarnings({ "resource", "unused" })
	public static void main(String[] args) {

		// Setup ultrasonic sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize
		// operating mode
		// 4. Create a buffer for the sensor data
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usValue = usSensor.getMode("Distance");
		float[] usData = new float[usValue.sampleSize()];

		// Setup color sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize
		// operating mode
		// 4. Create a buffer for the sensor data
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("ColorID");
		float[] colorData = new float[colorValue.sampleSize()];

		// setup the odometer, navigation, scanner, detector and display
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true);
		Navigation nav = new Navigation(odo);
		Detector det = new Detector(leftMotor, rightMotor, colorSensor, colorData, usSensor, usData);
		ObstacleAvoidance avoider = new ObstacleAvoidance(nav, det);
		Scanner scan = new Scanner(nav, odo, det, avoider);

		TextLCD t = LocalEV3.get().getTextLCD();

		int buttonChoice;

		do {
			// clear the display
			t.clear();

			// wait for user to choose action
			t.drawString("< Left  | Right > ", 0, 0);
			t.drawString("        |         ", 0, 1);
			t.drawString(" detect | move    ", 0, 2);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_RIGHT) {
			LCDInfo lcd = new LCDInfo(odo, det, scan);

			// perform the ultrasonic localization
			USLocalizer usl = new USLocalizer(leftMotor, rightMotor, odo, nav,
					usValue, usData, USLocalizer.LocalizationType.FALLING_EDGE);
			usl.doLocalization();
			
			// start sweeping the map to find object
			det.start();
			scan.start();
			avoider.start();

		} else if (buttonChoice == Button.ID_LEFT) {
			LCDInfo lcd = new LCDInfo(odo, det, scan);
			det.start();
		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);

	}

}
