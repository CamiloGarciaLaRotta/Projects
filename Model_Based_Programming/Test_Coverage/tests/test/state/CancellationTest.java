package state;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CancellationTest {

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
	
	// (a)
	@Test
	public void testCreatedCancel() {
		order.create();
		order.cancel();
		assertEquals(order.getState(), "cancelled");
	}
	
	// (c)
	@Test
	public void testQuoteOfferedCancel() {
		order.create();
		order.getShipmentRate();
		order.cancel();
		assertEquals(order.getState(), "cancelled");
	}
	
	// (e)
	@Test
	public void testConfirmedCancel() {
		order.create();
		order.getShipmentRate();
		order.confirm();
		order.cancel();
		assertEquals(order.getState(), "cancelled");
	}

}
