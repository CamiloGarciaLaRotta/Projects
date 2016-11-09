package ev3Search;

import ev3Search.Scanner.ScanState;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class Detector extends Thread {

	private Scanner scan;
	private SampleProvider colorSensor;
	private SampleProvider usSensor;
	private float[] colorData;
	private float[] usData;

	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	// distance and time boundaries
	private final float US_DETECTION_DIST = 20;
	private final float COLOR_DETECTION_DIST = 3;
	private final int TIME_PERIOD = 10;

	// color range of styrofoam
	private final int STYROFOAM[] = new int[] { 5, 9 };
	private static final float BLOCK_COLOR = 2;

	// filtering samples
	private int filterControl;
	private float lastDistance;
	private final int FILTER_OUT = 3;

	// status of detector to print
	public String status;
	public String object;

	public Detector(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,SampleProvider colorSensor, float[] colorData,
			SampleProvider usSensor, float[] usData) {
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.usSensor = usSensor;
		this.usData = usData;
		this.status = "Nothing";
		this.object = "";
		this.leftMotor=leftMotor;
		this.rightMotor=rightMotor;
		
	}

	public void run() {
		long correctionStart, correctionEnd;
		
		while (true) {
			correctionStart = System.currentTimeMillis();
			// object inside detection range
			if (getFilteredData() <= US_DETECTION_DIST) {
				this.status = "Oject Deteted";
				if (getFilteredData() <= COLOR_DETECTION_DIST) {
					// object's color inside styrofoam range
					if (getColor() > STYROFOAM[0] && getColor() < STYROFOAM[1]){
							Scanner.scanState = ScanState.FOUND;
							this.object = "STYROFOAM";
					} else if (getColor() > BLOCK_COLOR) {
						this.object = "OBSTACLE";
						Scanner.scanState = ScanState.OBSTACLE;
					}
				} else {
					this.object = "";
					Scanner.scanState = ScanState.OBJECT;
				}
			} else {
				this.status = "Nothing";
				this.object = "";
			}
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < TIME_PERIOD) {
				try {
					Thread.sleep(TIME_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
				}
			}
		}
	}

	// filtered distance
	public float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = (int) (usData[0] * 100.0);
		float result = 0;
		if (distance > 200 && filterControl < FILTER_OUT) {
			filterControl++;
			result = lastDistance;
		} else if (distance > 200) {
			result = 200;
		} else {
			filterControl = 0;
			result = distance;
		}
		lastDistance = distance;
		return result;
	}

	// self explanatory
	public float getColor() {
		colorSensor.fetchSample(colorData, 0);
		float color = colorData[0];
		return color;
	}

}
