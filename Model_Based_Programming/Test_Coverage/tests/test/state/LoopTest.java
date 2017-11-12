package state;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class LoopTest {

	// Order instance to be tested
	private static Order order;
	
	// instantiate object
	@BeforeClass
	public static void setUpBeforeClass() {
		order = new Order();
	}

	// destroy object
	@AfterClass
	public static void tearDownAfterClass() {
		order = null;
	}
	
	// (b)
	@Test
	public void testQuoteOfferedLoop() {
		order.create();
		order.getShipmentRate();
		order.getShipmentRate();
		order.getShipmentRate();
		assertEquals(order.getState(), "quoteOffered");
	}
	
	// (d)
	@Test
	public void testConfirmedLoop() {
		order.create();
		order.getShipmentRate();
		order.confirm();
		order.getConfirmedRate();
		order.getConfirmedRate();
		order.getConfirmedRate();
		assertEquals(order.getState(), "confirmed");
	}
	
	// (f)
	@Test
	public void testConfirmedCancel() {
		order.create();
		order.getShipmentRate();
		order.confirm();
		order.denyCancellations();
		order.getConfirmedRate();
		order.getConfirmedRate();
		order.getConfirmedRate();
		assertEquals(order.getState(), "cannotGetCancelled");
	}
}
