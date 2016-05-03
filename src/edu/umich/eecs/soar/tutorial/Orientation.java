package edu.umich.eecs.soar.tutorial;

import java.util.function.Function;

public enum Orientation {
	north(x -> x, y -> y+1), 
	east(x -> x+1, y -> y), 
	south(x -> x, y -> y-1), 
	west(x -> x-1, y -> y)
	; // assumed elsewhere to proceed in progressive clockwise order
	
	public static Orientation getOrientation(int o) {
		o = ((o + 4) % 4);
		for (Orientation or : values()) {
			if (o == or.ordinal()) {
				return or;
			}
		}

		return null;
	}
	
	private Orientation(Function<Integer, Integer> newX, Function<Integer, Integer> newY) {
		this.newX = newX;
		this.newY = newY;
	}
	
	public Orientation getRelativeOrientation(int offset) {
		return getOrientation(this.ordinal()+offset);
	}
	
	final public Function<Integer, Integer> newX;
	final public Function<Integer, Integer> newY;
}
