package org.kfm.chairskipping;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@gmail.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class Chair {
	
	private int id;
	private Chair previous;
	private Chair next;
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public Chair() {}
	
	/**
	 * <p>Class constructor that takes an unique identifier as a parameter.</p>
	 * 
	 * @param id The unique identifier of the <code>Chair</code>.
	 */
	public Chair(int id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Chair #" + id + "\n";
	}
	
	public int getId() {
		return id;
	}

	public Chair getNext() {
		return next;
	}

	public Chair getPrevious() {
		return previous;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNext(Chair next) {
		this.next = next;
	}

	public void setPrevious(Chair previous) {
		this.previous = previous;
	}
}
