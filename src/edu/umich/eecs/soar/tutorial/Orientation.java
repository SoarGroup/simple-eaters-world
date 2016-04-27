package edu.umich.eecs.soar.tutorial;

public enum Orientation {
	up, right, down, left;

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
