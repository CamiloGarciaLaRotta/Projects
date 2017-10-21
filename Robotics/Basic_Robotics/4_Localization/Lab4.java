import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;

public class Lab4 {

	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	// Ultrasonic sensor port connected to input S1
	// Color sensor port connected to input S2

	public static final int MOTOR_LOW = 60; // (deg/sec)
	public static final int MOTOR_HIGH = 150; // (deg/sec)

	// wheel spacing and radius
	public static final double TRACK = 8.3;// 8.3
	public static final double WHEEL_RADIUS = 2.08;
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
		// Because we don't bother to close this resource
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usValue = usSensor.getMode("Distance"); // colorValue
																// provides
																// samples from
																// this instance
		float[] usData = new float[usValue.sampleSize()]; // colorData is the
															// buffer in which
															// data are returned

		// Setup color sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize
		// operating mode
		// 4. Create a buffer for the sensor data
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("Red"); // colorValue
																// provides
																// samples from
																// this instance
		float[] colorData = new float[colorValue.sampleSize()]; // colorData is
																// the buffer in
																// which data
																// are returned

		// setup the odometer and display
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true);
		Navigation nav = new Navigation(odo);

		TextLCD t = LocalEV3.get().getTextLCD();

		int buttonChoice;

		do {
			// clear the display
			t.clear();

			// wait for user to choose action
			t.drawString("< Left  | Right > ", 0, 0);
			t.drawString("        |         ", 0, 1);
			t.drawString("        | Localize", 0, 2);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT
				&& buttonChoice != Button.ID_UP);

		if (buttonChoice == Button.ID_RIGHT) {
			LCDInfo lcd = new LCDInfo(odo);

			// perform the ultrasonic localization
			USLocalizer usl = new USLocalizer(leftMotor, rightMotor, odo, nav,
					usValue, usData, USLocalizer.LocalizationType.FALLING_EDGE);
			usl.doLocalization();

			Sound.beep();
			Sound.beep();
			Button.waitForAnyPress();
			// perform the light localization
			LightLocalizer lsl = new LightLocalizer(leftMotor, rightMotor, nav,
					odo, colorValue, colorData);
			lsl.doLocalization();
		} else if (buttonChoice == Button.ID_LEFT) {
			LCDInfo lcd = new LCDInfo(odo);
			leftMotor.flt();
			rightMotor.flt();
		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
		System.exit(0);

	}

}
