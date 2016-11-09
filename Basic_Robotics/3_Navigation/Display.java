package ev3Navigation;

import lejos.hardware.lcd.TextLCD;

public class Display extends Thread {
	// private static final long DISPLAY_PERIOD = 50;

	private static final long DISPLAY_PERIOD = 250;
	private UltrasonicPoller usPoller;
	private Odometer odometer;
	private TextLCD t;
	private Navigator navigator;

	// constructor
	public Display(Odometer odometer, Navigator navigator,
			UltrasonicPoller usPoller, TextLCD t) {
		this.odometer = odometer;
		this.navigator = navigator;
		this.usPoller = usPoller;
		this.t = t;
	}

	// run method (required for Thread)
	public void run() {

		long displayStart, displayEnd;
		double[] position = new double[3];

		// clear the display once
		t.clear();

		while (true) {
			displayStart = System.currentTimeMillis();

			// clear the lines for displaying odometry information
			t.drawString("Goal: " + navigator.destToString(), 0, 0);
			t.drawString("X:              ", 0, 1);
			t.drawString("Y:              ", 0, 2);
			t.drawString("Ti:              ", 0, 3);
			t.drawString("STATE: " + navigator.stateToString() + "   ", 0, 4);
			t.drawString(navigator.thetaToString() + "   ", 0, 5);
			t.drawString("Distnce: " + navigator.distanceToString() + "    ",
					0, 6);
			t.drawString("usPoller: " + usPoller.getDistance(), 0, 7);

			// get the odometry information
			odometer.getPosition(position, new boolean[] { true, true, true });

			// transform theta to deg
			position[2] = Math.toDegrees(position[2]);

			// display odometry information
			for (int i = 0; i < 3; i++) {
				t.drawString(formattedDoubleToString(position[i], 2), 3, i + 1);
			}

			// throttle the OdometryDisplay
			displayEnd = System.currentTimeMillis();
			if (displayEnd - displayStart < DISPLAY_PERIOD) {
				try {
					Thread.sleep(DISPLAY_PERIOD - (displayEnd - displayStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that OdometryDisplay will be interrupted
					// by another thread
				}
			}
		}
	}

	private static String formattedDoubleToString(double x, int places) {
		String result = "";
		String stack = "";
		long t;

		// put in a minus sign as needed
		if (x < 0.0)
			result += "-";

		// put in a leading 0
		if (-1.0 < x && x < 1.0)
			result += "0";
		else {
			t = (long) x;
			if (t < 0)
				t = -t;

			while (t > 0) {
				stack = Long.toString(t % 10) + stack;
				t /= 10;
			}

			result += stack;
		}

		// put the decimal, if needed
		if (places > 0) {
			result += ".";

			// put the appropriate number of decimals
			for (int i = 0; i < places; i++) {
				x = Math.abs(x);
				x = x - Math.floor(x);
				x *= 10.0;
				result += Long.toString((long) x);
			}
		}

		return result;
	}
}
