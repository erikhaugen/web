package com.livescribe.base;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RollingBuffer<K> {

	private List<K> buffer = new LinkedList<K>();
	private int bufferSize;
	
	public RollingBuffer(int size) {
		updateSize(size);
	}
	
	public void add(K entry) {
		buffer.add(entry);
		if ( buffer.size() > bufferSize ) {
			buffer.remove(0);
		}
	}
	
	public int getSize() {
		return this.bufferSize;
	}
	
	public void updateSize(int size) {
		if ( size < 2 ) {
			throw new IllegalArgumentException("RollingBuffer parameter size must be greater than 1");
		}
		this.bufferSize = size;
	}
	
	public List<K> getAllEntries() {
		return Collections.unmodifiableList(buffer);
	}
	
	public String printEntries(CharSequence prefix, CharSequence suffix) {
		StringBuilder sb = new StringBuilder();
		prefix = ( prefix == null ? "" : prefix);
		suffix = ( suffix == null ? "" : suffix);

		for (K entry: buffer) {
			sb.append(prefix);
			sb.append(entry.toString());
			sb.append(suffix);
		}
		return sb.toString();
	}
}
