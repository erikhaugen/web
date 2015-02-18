import java.util.HashSet;
import java.util.Set;

/**
 * 
 */

/**
 * @author Kevin
 *
 */
public class Piece {
	
	private int id;
	private Set<Coord> coords = new HashSet<Coord>();
	
	public Piece(int id) {
		this.id = id;
	}
	
	public void add(int x, int y) {
		Coord c = new Coord(x, y);
		this.coords.add(c);
	}

	public int getId() {
		return id;
	}

	public Set<Coord> getCoords() {
		return coords;
	}
}
