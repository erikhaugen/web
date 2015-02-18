import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * 
 */

/**
 * @author Kevin
 *
 */
public class Solution {

	private int[][] puzzle;
	private List<Piece> pieces = null;
	private Set<Coord> covered = null;
	
	public Solution(int w, int h) {
		puzzle = new int[h][w];
		covered = new HashSet<Coord>();
	}
	
	public Solution(List<Piece> pieces) {
		this.pieces = pieces;
	}
	
	public boolean fits(Piece p) {
		
		//	Visitor pattern???
		if ()
	}
	
	public void place(Piece p) {
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Scanner scanner = new Scanner(System.in);
		
		//	Read the puzzle dimensions.
		String hwk = scanner.nextLine();
		String[] hwkParts = hwk.split(" ");
		int w = Integer.parseInt(hwkParts[0]);
		int h = Integer.parseInt(hwkParts[1]);
		int k = Integer.parseInt(hwkParts[2]);
		
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		
		//	Read in the pieces.
		int pieceCount = 1;
		while (pieceCount <= k) {
			int coordCount = scanner.nextInt();
			Piece p = new Piece(pieceCount);
			int i = 0;
			while (i < coordCount) {
				int x = scanner.nextInt();
				int y = scanner.nextInt();
				p.add(x, y);
				i++;
			}
			pieces.add(p);
			pieceCount++;
		}
		scanner.close();
	
		int[][] matrix = new int[h][w];
//		Solution s = new Solution(pieces);
		Solution s = new Solution(w, h);
		for (Piece p : pieces) {
			
		}
	}
	
}
