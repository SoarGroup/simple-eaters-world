package edu.umich.eecs.soar.tutorial;

import java.awt.Color;

public enum MapObject {
	white(Color.WHITE),
	brown(new Color(112, 83, 7)),
	red(Color.RED), 
	blue(Color.BLUE), 
	green(Color.GREEN),
	purple(new Color(137, 4, 177)),

	wall(Color.GRAY)
	;

	private MapObject(Color color) {
		this.color = color;
	}

	public final Color color;
}
