package edu.umich.eecs.soar.tutorial;

public enum Orientation {
	north, east, south, west;

	public static Orientation getOrientation(int o) {
		o = ((o + 4) % 4);
		for (Orientation or : values()) {
			if (o == or.ordinal()) {
				return or;
			}
		}

		return null;
	}
}
