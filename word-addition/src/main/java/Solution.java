import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 */

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@gmail.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class Solution {

	/**
	 * 
	 */
	public Solution() {
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		List<String> words = readWords();
		List<List> wordLists = convertToLists(words);
		
		Solution s = new Solution();
		s.solve(words);
	}

	private static List<List> convertToLists(List<String> words) {

		
		return null;
	}

	private void solve(List<String> words) {
		
		
	}

	private void assignDigit() {
		
	}
	
	private boolean checkMath() {
		return false;
	}
	
	private static List<String> readWords() {
		
		List<String> words = new LinkedList<String>();
		Scanner scanner = new Scanner(System.in);
		
		String line = null;
		while ((line = scanner.nextLine()) != "") {
			words.add(line.trim());
		}
		return words;
	}
}
