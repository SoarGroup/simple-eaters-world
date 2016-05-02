package edu.umich.eecs.soar.tutorial;

import java.awt.Color;

public enum MapObject {
	red(Color.decode("#F16745")),
	green(Color.decode("#7BC8A4")),
	blue(Color.decode("#4CC3D9")), 
	purple(Color.decode("#93648D")),

	wall(Color.decode("#404040"))
	;

	private MapObject(Color color) {
		this.color = color;
	}

	public final Color color;
}
