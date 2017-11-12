package counter;


import static org.junit.Assert.*;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CounterTest {
	// global expected variable to assert
	private static String[] testedStrings;
	private static int testedOutput;
	private static int expectedOutput;
	
	// avoid side effects of asserted variables
	@BeforeClass
	public static void setUpBeforeClass() {
		testedStrings = null;
		testedOutput = 0;
		expectedOutput = 0;
	}

	// avoid side effects of asserted variables
	@AfterClass
	public static void tearDownAfterClass() {
		testedStrings = null;
		testedOutput = 0;
		expectedOutput = 0;
	}
	
	// 1. Test behavior when input array is null
	@Test
	public void testNullInput() {
		testedStrings = null;
		testedOutput = Counter.is_inCounter(testedStrings);
		expectedOutput = -1;
		
		assertEquals(testedOutput, expectedOutput);
	}
	
	// 2. Test behavior when input array is empty
	@Test
	public void testEmptyInput1() {
		testedStrings = new String[]{};
		testedOutput = Counter.is_inCounter(testedStrings);
		expectedOutput = 0;
		
		assertEquals(testedOutput, expectedOutput);
	}
	
	// 3. Test behavior when input array is non-empty and has no occurrences
	@Test
	public void testNoOccurences() {
		testedStrings = new String[] {"hello", "world", "loco"};
		testedOutput = Counter.is_inCounter(testedStrings);
		expectedOutput = 0;
		
		assertEquals(testedOutput, expectedOutput);
	}
	
	// 4. Test behavior when input array is non-empty and has an empty string
	@Test
	public void testNoChar() {
		testedStrings = new String[] {""};
		testedOutput = Counter.is_inCounter(testedStrings);
		expectedOutput = 0;
		
		assertEquals(testedOutput, expectedOutput);
	}
	
	// 5. Test behavior when input array is non-empty and has single character strings
	@Test
	public void testSingleChar() {
		testedStrings = new String[] {"h", "w"};
		testedOutput = Counter.is_inCounter(testedStrings);
		expectedOutput = 0;
		
		assertEquals(testedOutput, expectedOutput);
	}
	
	// 6. Test behavior when input array is non-empty and has only "is" occurrences
	// "is" occurrences at middle, beginning and end of string
	@Test
	public void testIsOccurences() {
		testedStrings = new String[] {"helislo", "isworld", "locois"};
		testedOutput = Counter.is_inCounter(testedStrings);
		expectedOutput = 3;
		
		assertEquals(testedOutput, expectedOutput);
	}
	
	// 7. Test behavior when input array is non-empty and has only "in" occurrences
	// "in" occurrences at middle, beginning and end of string
	@Test
	public void testInOccurences() {
		testedStrings = new String[] {"helinlo", "inworld", "locoin"};
		testedOutput = Counter.is_inCounter(testedStrings);
		expectedOutput = 3;
		
		assertEquals(testedOutput, expectedOutput);
	}
	
	// 8. Test behavior when input array is non-empty and has "is" and "in" occurrences
	// "is" and "in" occurrences at middle, beginning and end of string
	// "is" and "in" occurrences together(isin and inis) and separate
	@Test
	public void testIsInOccurences() {
		testedStrings = new String[] {"helisinlo", "isinworld", "locoisin",
										"helinislo", "inisworld", "locoinis",
										"helisXinlo", "isXinworld", "locoisXin"};
		testedOutput = Counter.is_inCounter(testedStrings);
		expectedOutput = 18;
		
		assertEquals(testedOutput, expectedOutput);
	}
}
