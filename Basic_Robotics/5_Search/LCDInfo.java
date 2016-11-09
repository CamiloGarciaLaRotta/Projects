package ev3Search;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class LCDInfo implements TimerListener {
	public static final int LCD_REFRESH = 100;
	private Odometer odo;

	private Timer lcdTimer;
	private TextLCD LCD = LocalEV3.get().getTextLCD();;
	private Detector detector;
	private Scanner scanner;
	// arrays for displaying data
	private double[] pos;

	public LCDInfo(Odometer odo, Detector detector, Scanner scanner) {
		this.odo = odo;
		this.detector = detector;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		this.scanner = scanner;
		// initialize the arrays for displaying data
		pos = new double[3];

		// start the timer
		lcdTimer.start();
	}

	public void timedOut() {
		odo.getPosition(pos);
		LCD.clear();
		LCD.drawString("X: " + (int) (pos[0]), 0, 0);
		LCD.drawString("Y: " + (int) (pos[1]), 0, 1);
		LCD.drawString("H: " + (int) (pos[2]), 0, 2);
		LCD.drawString("D: " + (int) detector.getFilteredData() + "     ",
				0, 3);
//		LCD.drawString("Search: " + detector.status + "   ", 0, 4);
//		LCD.drawString(detector.object + "    ", 0, 5);
//		LCD.drawString("COLOR: " + detector.getColor(), 0, 6);
//		LCD.drawString("STATUS: " + USLocalizer.status + "   ", 0, 4);
		LCD.drawString("State: " + Scanner.scanState + "    ", 0, 5);
		LCD.drawString("A:" + (int) USLocalizer.angleA + " B:" + (int) USLocalizer.angleB , 0, 6);
	}
}
