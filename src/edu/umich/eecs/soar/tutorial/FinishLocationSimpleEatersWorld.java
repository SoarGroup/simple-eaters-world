package edu.umich.eecs.soar.tutorial;

import sml.Agent;
import sml.Identifier;

public class FinishLocationSimpleEatersWorld extends SimpleEatersWorld {
	final private int finalX, finalY; 
	
	public FinishLocationSimpleEatersWorld(Agent agent, MapObject[][] map, Orientation initialOrientation, int initialX, int initialY, int sleepMsecs, int finalX, int finalY) {
		super(agent, map, initialOrientation, initialX, initialY, sleepMsecs);
		this.finalX = finalX;
		this.finalY = finalY;
	}
	
	protected boolean isDone() {
		return (foodCount == eaten && x == finalX && y == finalY);
	}

	@Override
	protected void _updateSoar() {
		_updateEssentialsSoar();
		final Identifier inputLink = a.GetInputLink();
		if (x == finalX && y == finalY) {
			_createWME(inputLink, "finish", "here");
		}
		_createWME(inputLink, "food-remaining", foodCount - eaten);
	}
}
