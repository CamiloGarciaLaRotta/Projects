package counter;

/**
 * @author Camilo Garcia La Rotta
 */
public class Counter {

	/**
	 * Returns the total amount of "is" and "in" 
	 * substrings found in the input array of strings
	 * <br>
	 * If the input array is null the method returns -1
	 * <br>
	 * If the input array is empty or no occurrences are found the method returns 0
	 * 
	 * @param strings String array to inspect
	 * @return integer amount of occurrences found
	 */
	public static int is_inCounter(String[] strings) {
		// substring counter
		int is_inCount = 0;
		
		// verify validity of input
		if(strings == null) {
			return -1;
		}
		
		// iterate over all input strings
		for (String s: strings) {
			for(int i = 0; i < s.length()-1; i++) {
				if (s.charAt(i) == 'i') {
					if (s.charAt(i+1) == 's' || s.charAt(i+1) == 'n') {
						is_inCount++;
					}
				}
			}
		}
		return is_inCount;
	}
}
