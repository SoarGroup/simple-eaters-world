package edu.umich.eecs.soar.tutorial;

import sml.Agent;

public class FullSimpleEatersWorld extends SimpleEatersWorld {

	public FullSimpleEatersWorld(Agent agent, MapObject[][] map, Orientation initialOrientation, int initialX, int initialY, int sleepMsec) {
		super(agent, map, initialOrientation, initialX, initialY, sleepMsec);
	}

	@Override
	protected boolean isDone() {
		return (foodCount == eaten);
	}
	
	@Override
	protected void _updateSoar() {
		_updateEssentialsSoar();
	}

}
