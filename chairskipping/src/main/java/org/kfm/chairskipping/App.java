/**
 * 
 */
package org.kfm.chairskipping;

import java.util.Map;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@gmail.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class App {

	private int numberToSkip = 1;
	private Chair head;
	
	/**
	 * <p>Constructs a doubly-linked list of <code>chairCount</code> number of 
	 * <code>Chair</code> objects.</p>
	 * 
	 * @param chairCount The number of <code>Chair</code> objects to create.
	 */
	public App(int chairCount) {
		System.out.println("Chair count:  " + chairCount);
		
		head = new Chair(1);
		Chair prev = head;
		for (int i = 2; i <= chairCount; i++) {
			Chair ch = new Chair(i);
			prev.setNext(ch);
			ch.setPrevious(prev);
			prev = ch;
		}
		prev.setNext(head);
		head.setPrevious(prev);
	}
	
	/**
	 * <p>Returns the unique identifier of the last remaining chair.</p>
	 * 
	 * @return the unique identifier of the last remaining chair.
	 */
	public int findSurvivor() {
		
		Chair ch = head;
		
		//	Repeat until the current Chair object is pointing at itself, which
		//	will indicate it is the final remaining chair.
		while (!ch.getNext().equals(ch)) {
			
			//	First, remove the current chair from the list.
			Chair next = remove(ch);
			
			//	Skip over the current number-of-chairs-to-skip.
			ch = skip(next);
		}
		return ch.getId();
	}

	public int findSurvivor2() {

		Chair ch = head;
		
		//	Repeat until the current Chair object is pointing at itself, which
		//	will indicate it is the final remaining chair.
		while (!ch.getNext().equals(ch)) {
			
			//	First, remove the current chair from the list.
			Chair prev = ch.getPrevious();
			Chair next = ch.getNext();
			prev.setNext(next);
			next.setPrevious(prev);
			
			//	Skip over the current number-of-chairs-to-skip.
			for (int i = 0; i < numberToSkip; i++) {
				next = next.getNext();
			}
			ch = next;
			numberToSkip++;
		}
		return ch.getId();
	}

	/**
	 * <p>Removes the given <code>Chair</code> from the ring of chairs.</p>
	 * 
	 * @param ch The chair to remove.
	 * 
	 * @return the chair that came after the one removed.
	 */
	private Chair remove(Chair ch) {
		
		Chair prev = ch.getPrevious();
		Chair next = ch.getNext();
		prev.setNext(next);
		next.setPrevious(prev);
		
		return next;
	}
	
	/**
	 * <p>Skips over the current number of chairs and returns the next chair
	 * that will be removed.</p>
	 * 
	 * Increments the number of chairs to skip by 1 for each invocation.
	 * 
	 * @param ch The chair to begin skipping from.
	 * 
	 * @return the chair after the skip.
	 */
	private Chair skip(Chair ch) {
		
		for (int i = 0; i < numberToSkip; i++) {
			ch = ch.getNext();
		}
		numberToSkip++;
		return ch;
	}

	/**
	 * <p>Main entry point of program.</p>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		App app = null;
		
		//	Set a default number of chairs.
		if (args.length == 0) {
			app = new App(100);
			
		//	Otherwise, use the provided number of chairs.
		} else {
			String countStr = args[0];
			int count = 0;
			try {
				count = Integer.parseInt(countStr);
			} catch (NumberFormatException e) {
				System.out.println("Invalid parameter provided:  " + args[0] + "\n");
				System.out.println("Usage:  $ java ");
				e.printStackTrace();
				System.exit(1);
			}
			app = new App(count);
		}
		long start = System.currentTimeMillis();
		int survivor = app.findSurvivor();
		long end = System.currentTimeMillis();
		long duration = end - start;
		
		System.out.println("The last surviving chair is #" + survivor);
		System.out.println("Duration:  " + duration + " milliseconds.");
	}
}
