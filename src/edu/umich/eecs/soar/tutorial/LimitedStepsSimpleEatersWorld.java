package edu.umich.eecs.soar.tutorial;

import sml.Agent;

public class LimitedStepsSimpleEatersWorld extends SimpleEatersWorld {
	final private int maxSteps;
	
	public LimitedStepsSimpleEatersWorld(Agent agent, MapObject[][] map, Orientation initialOrientation, int initialX, int initialY, int sleepMsecs, int maxSteps) {
		super(agent, map, initialOrientation, initialX, initialY, sleepMsecs);
		this.maxSteps = maxSteps;
	}

	@Override
	protected boolean isDone() {
		return (steps == maxSteps);
	}

	@Override
	protected void _updateSoar() {
		_updateEssentialsSoar();
	}
}
