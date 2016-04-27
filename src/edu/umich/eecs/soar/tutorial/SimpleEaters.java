package edu.umich.eecs.soar.tutorial;

import java.util.concurrent.CountDownLatch;

import javax.swing.JOptionPane;

import sml.Agent;
import sml.Kernel;
import sml.smlRunStepSize;

public class SimpleEaters {

	public static void main(String[] args) {
		final int kernelPort = 27314;
		final String agentName = "Eva";
		final int agentRandomSeed = 41372;

		final Orientation initialOrientation = Orientation.right;
		final int initialX = 1;
		final int initialY = 2;

		final MapObject[][] map = { 
			{ MapObject.purple,			null,					MapObject.red, 			MapObject.green },
			{ MapObject.purple, 		MapObject.red, 			MapObject.red, 			MapObject.wall },
			{ MapObject.purple, 		MapObject.wall,			MapObject.red, 			MapObject.green }, 
		};
		
		final int sleepMsecs = 20;

		final Kernel kernel = Kernel.CreateKernelInNewThread(kernelPort);
		final Agent agent = kernel.CreateAgent(agentName);
		agent.ExecuteCommandLine("srand " + agentRandomSeed);

		final CountDownLatch latch = new CountDownLatch(1);
		final SimpleEatersWorld world = new FullSimpleEatersWorld(latch, agent, map, initialOrientation, initialX, initialY, sleepMsecs);
		world.setPoints(MapObject.purple, 10);
		world.setPoints(MapObject.red, 5);

		agent.SpawnDebugger(kernelPort, "lib/soar/SoarJavaDebugger.jar");
		agent.LoadProductions("agent.soar");
		agent.RunSelf(1, smlRunStepSize.sml_ELABORATION);

		try {
			latch.await();
		} catch (InterruptedException e) {
		}
		
		JOptionPane.showMessageDialog(null, "Score: " + world.getScore(), "Task Complete", JOptionPane.INFORMATION_MESSAGE);

		agent.KillDebugger();
		kernel.Shutdown();
		System.exit(0);
	}

}
