package ev3Navigation;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Lab3 {

	// //// GENERAL INSTANCES //////

	// for object avoidance
	private static final Port usPort = LocalEV3.get().getPort("S1");
	private static final SensorModes usSensor = new EV3UltrasonicSensor(usPort);
	private static SampleProvider usDistance = usSensor.getMode("Distance");
	private static float[] usData = new float[usDistance.sampleSize()];
	private static UltrasonicPoller usPoller = new UltrasonicPoller(usDistance,
			usData);

	// for movement
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	// for navigation
	private static final Odometer odometer = new Odometer(leftMotor, rightMotor);
	private static final Navigator navigator = new Navigator(odometer, usPoller, leftMotor, rightMotor);
	private static final TextLCD t = LocalEV3.get().getTextLCD();
	private static final Display display = new Display(odometer, navigator, usPoller, t);

	// //// CONSTANTS //////

	// Navigator
	public static final int[][] POINTS = { { 60, 30 }, { 30, 30 }, { 30, 60 }, { 60, 0 } };
	public static final int[][] POINTS_OA = { { 0, 60 }, { 60, 0 } };
	
	// Ultrasonic Sensor
	public static final int BC = 20; 			// BandCenter (cm)
	public static final int BW = 3; 			// BandWidth (cm)
	public static final int MOTOR_LOW = 150; 	// (deg/sec)
	public static final int MOTOR_HIGH = 250; 	// (deg/sec)

	// Odometer
	public static final double TRACK = 15.9;
	public static final double WHEEL_RADIUS = 2.1;

	// //// ENTRY POINT //////

	public static void main(String[] args) {
		int buttonChoice;

		do {
			// clear the display
			t.clear();

			// wait for user to choose action
			t.drawString("< Left    | Right > ", 0, 0);
			t.drawString("          |         ", 0, 1);
			t.drawString("  OA      | Navigate", 0, 2);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT
				&& buttonChoice != Button.ID_UP);

		if (buttonChoice == Button.ID_RIGHT) {

			// start all threads
			usPoller.start();
			odometer.start();
			navigator.start();
			display.start();

			completeCourse(POINTS);

		} else if (buttonChoice == Button.ID_LEFT) {
			usPoller.start();
			odometer.start();
			navigator.start();
			display.start();

			completeCourse(POINTS_OA);
		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
		System.exit(0);
	}

	// Travel to given set of points
	private static void completeCourse(final int[][] POINTS) {
		(new Thread() {
			public void run() {
				for (int[] point : POINTS) {
					navigator.travelTo(point[0], point[1]);
					while (navigator.isNavigating) {
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							e.printStackTrace();
						};
					}
				}
			}
		}).start();
	}
}
