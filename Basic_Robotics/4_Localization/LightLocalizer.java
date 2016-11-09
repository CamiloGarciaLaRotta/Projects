import java.io.FileNotFoundException;
import java.io.PrintWriter;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private Odometer odo;
	private SampleProvider colorSensor;
	private float[] colorData;
	private Navigation navigator;
	private static int lightSensorDistance = 15;
	public static int rotationSpeed = 50;
	// black color value
	private static final int BLACK = 270;

	public LightLocalizer(EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor, Navigation navigator,
			Odometer odo, SampleProvider colorSensor, float[] colorData) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.navigator = navigator;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;

	}

	public void doLocalization() {
		// writer to record xDelta and yDelta
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("123.out");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// starting x,y from us localizer are wrong(needs calibration)
		// when done travel to (0,0) and turn to 0 degrees

		leftMotor.setSpeed(Lab4.MOTOR_LOW);
		rightMotor.setSpeed(Lab4.MOTOR_LOW);
		leftMotor.forward();
		rightMotor.forward();

		// run until detects a blackline
		// since we start at 0 deg, this line is -y
		while (getCurcolor() > BLACK) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		leftMotor.stop();
		rightMotor.stop();

		leftMotor.setSpeed(rotationSpeed);
		rightMotor.setSpeed(rotationSpeed);

		// now go backwards a fixed distance
		// lightSensorDistance needs to + 6 because we dont want the light
		// sensor
		// to touch the black line
		leftMotor.rotate(
				-convertDistance(Lab4.WHEEL_RADIUS, lightSensorDistance + 6),
				true);
		rightMotor.rotate(
				-convertDistance(Lab4.WHEEL_RADIUS, lightSensorDistance + 6),
				false);

		// now rotate to do the same thing in the X.
		leftMotor.setSpeed(rotationSpeed);
		rightMotor.setSpeed(rotationSpeed);

		navigator.turnTo(90, true);
		// theta is now 90.
		// set the speeds of the motors

		leftMotor.setSpeed(Lab4.MOTOR_LOW);
		rightMotor.setSpeed(Lab4.MOTOR_LOW);
		leftMotor.forward();
		rightMotor.forward();

		// now we hit -x
		while (getCurcolor() > BLACK) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		rightMotor.stop();
		leftMotor.stop();

		leftMotor.setSpeed(rotationSpeed);
		rightMotor.setSpeed(rotationSpeed);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// back to staring point
		leftMotor.rotate(
				-convertDistance(Lab4.WHEEL_RADIUS, lightSensorDistance + 6),
				true);
		rightMotor.rotate(
				-convertDistance(Lab4.WHEEL_RADIUS, lightSensorDistance + 6),
				false);

		rightMotor.stop();
		leftMotor.stop();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int numLines = 0;
		double[] anglesOfLines = new double[4];
		// [0] -x
		// [1] y
		// [2] x
		// [3] -y
		navigator.turnTo(90, true);
		leftMotor.setSpeed(rotationSpeed);
		rightMotor.setSpeed(rotationSpeed);
		leftMotor.forward();
		rightMotor.backward();

		while (numLines < 4) {
			if (getCurcolor() <= BLACK) {
				anglesOfLines[numLines] = odo.getAng();
				writer.write((int) anglesOfLines[numLines] + "\n");
				numLines++;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		rightMotor.stop();
		leftMotor.stop();


		leftMotor.setSpeed(rotationSpeed);
		rightMotor.setSpeed(rotationSpeed);

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// calculate the deltas.
		double deltaY = anglesOfLines[3] - anglesOfLines[1];
		double deltaX = anglesOfLines[2] - anglesOfLines[0];
		double yValue = -1 * lightSensorDistance
				* Math.cos(Math.PI * deltaX / (2 * 180));
		double xValue = -1 * lightSensorDistance
				* Math.cos(Math.PI * deltaY / (2 * 180));
		navigator.turnTo(0, true);
		writer.write(xValue + "\n");
		writer.write(yValue + "\n");
		writer.close();

		odo.setPosition(new double[] { xValue, yValue, 0 }, new boolean[] {
				true, true, true });
		navigator.travelTo(0, 0);
		navigator.turnTo(0, true);
		Sound.beep();
		leftMotor.setSpeed(rotationSpeed);
		rightMotor.setSpeed(rotationSpeed);
		leftMotor.forward();
		rightMotor.backward();
		while (getCurcolor() > BLACK) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		leftMotor.stop();
		rightMotor.stop();
		
		leftMotor.rotate(
				convertDistance(Lab4.WHEEL_RADIUS,2),
				true);
		rightMotor.rotate(
				convertDistance(Lab4.WHEEL_RADIUS,2),
				false);

		rightMotor.stop();
		leftMotor.stop();
		
		odo.setPosition(new double[] { xValue, yValue, 0 }, new boolean[] {
				true, true, true });
	}

	private float getCurcolor() {
		colorSensor.fetchSample(colorData, 0);
		float currColor = colorData[0] * 1000;
		return currColor;
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

}
