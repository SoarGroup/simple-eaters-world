package edu.umich.eecs.soar.tutorial;

import java.util.concurrent.CountDownLatch;

import sml.Agent;

public class LimitedStepsSimpleEatersWorld extends SimpleEatersWorld {
	final private int maxSteps;
	
	public LimitedStepsSimpleEatersWorld(CountDownLatch latchDone, Agent agent, MapObject[][] map, Orientation initialOrientation, int initialX, int initialY, int sleepMsecs, int maxSteps) {
		super(latchDone, agent, map, initialOrientation, initialX, initialY, sleepMsecs);
		this.maxSteps = maxSteps;
	}

	@Override
	protected boolean isDone() {
		return (steps == maxSteps);
	}
}
