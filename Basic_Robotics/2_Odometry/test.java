package ev3Odometer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

class test {

	/**
	 * @param args
	 */
	File f = new File("1.out");
	// static FileOutputStream out = new FileOutputStream("1.text");
	static Port portColor = LocalEV3.get().getPort("S1");
	static SensorModes myColor = new EV3ColorSensor(portColor);
	static int numSamples = 0;
	static SampleProvider myColorSample = myColor.getMode("Red");

	static float[] sampleColor = new float[myColor.sampleSize()];

	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(
			LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(
			LocalEV3.get().getPort("D"));

	public static void main(String[] args) throws IOException,
			InterruptedException {
		// // TODO Auto-generated method stub
		// Odometer odometer = new Odometer(leftMotor, rightMotor);
		// OdometryCorrection oc = new OdometryCorrection(odometer);
		// odometer.start();
		// oc.start();
		// (new Thread() {
		// public void run() {
		// SquareDriver.drive(leftMotor, rightMotor, 2.1, 2.1, 15.8);
		// }
		// }).start();
		FileOutputStream out = new FileOutputStream("1.text");
		boolean rolling = true; // cart moves while true
		int status;

		while (rolling) {
			status = Button.readButtons();
			if ((status == Button.ID_ENTER)) {
				break;
			}
			myColorSample.fetchSample(sampleColor, 0);
			numSamples++;
			// System.out.println(numSamples + ", " + sampleColor[0] * 1000);
			out.write((int) (sampleColor[0] * 1000));
			System.out.println(sampleColor[0] * 1000);
			Thread.sleep(100); // sleep 'till next cycle
		}

		out.close();

	}

}
