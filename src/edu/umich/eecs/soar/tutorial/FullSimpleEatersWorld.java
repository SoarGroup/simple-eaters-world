package edu.umich.eecs.soar.tutorial;

import java.util.concurrent.CountDownLatch;

import sml.Agent;

public class FullSimpleEatersWorld extends SimpleEatersWorld {

	public FullSimpleEatersWorld(CountDownLatch latchDone, Agent agent, MapObject[][] map, Orientation initialOrientation, int initialX, int initialY, int sleepMsec) {
		super(latchDone, agent, map, initialOrientation, initialX, initialY, sleepMsec);
	}

	@Override
	protected boolean isDone() {
		return (foodCount == eaten);
	}

}
