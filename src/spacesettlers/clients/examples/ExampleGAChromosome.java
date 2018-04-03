package spacesettlers.clients.examples;

import java.util.HashMap;
import java.util.Random;

import spacesettlers.actions.AbstractAction;
import spacesettlers.actions.DoNothingAction;
import spacesettlers.actions.MoveToObjectAction;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

/**
 * An example chromosome for a space settlers agent using genetic algorithms / evolutionary computation
 * 
 * @author amy
 *
 */
public class ExampleGAChromosome {
	private HashMap<ExampleGAState, AbstractAction> policy;
	private int[] thresholds;
	private int maxThreshold = 5000;
	
	public ExampleGAChromosome() {
		policy = new HashMap<ExampleGAState, AbstractAction>();
		thresholds = getRandomThresholds();
	}

	/**
	 * Returns either the action currently specified by the policy or randomly selects one if this is a new state
	 * 
	 * @param currentState
	 * @return
	 */
	public AbstractAction getCurrentAction(Toroidal2DPhysics space, Ship myShip, ExampleGAState currentState, Random rand) {
		if (!policy.containsKey(currentState)) {
//			// randomly chose to either do nothing or go to the nearest
//			// asteroid.  Note this needs to be changed in a real agent as it won't learn 
//			// much here!
//			if (rand.nextBoolean()) {
//				policy.put(currentState, new DoNothingAction());
//			} else {
//				//System.out.println("Moving to nearestMineable Asteroid " + myShip.getPosition() + " nearest " + currentState.getNearestMineableAsteroid().getPosition());
//				policy.put(currentState, new MoveToObjectAction(space, myShip.getPosition(), currentState.getNearestMineableAsteroid()));
//			}
			if (myShip.getResources().getTotal() < thresholds[0])
			{
				policy.put(currentState, new MoveToObjectAction(space, myShip.getPosition(), currentState.getNearestMineableAsteroid()));
			}
			else if (myShip.getEnergy() < thresholds[1])
			{
				policy.put(currentState, new MoveToObjectAction(space, myShip.getPosition(), currentState.getNearestBeacon()));
			}
			else if (myShip.getResources().getTotal() > thresholds[2])
			{
				policy.put(currentState, new MoveToObjectAction(space, myShip.getPosition(), currentState.getNearestBase()));
			}
			else if (myShip.getNumCores() < thresholds[3])
			{
				policy.put(currentState, new MoveToObjectAction(space, myShip.getPosition(), currentState.getNearestCore()));
			}
		}

		return policy.get(currentState);

	}
	
	public int[] getRandomThresholds()
	{
		//0 = asteroid; 1 = beacon; 2 = base; 3 = core
		int[] result = new int[4];
		Random rand = new Random();
		// Idea is to get an asteroid if num resources < result[0]
		result[0] = (int) rand.nextDouble() * maxThreshold;
		// Get beacon if energy < result[1]
		result[1] = (int) rand.nextDouble() * maxThreshold;
		// Go back to base if num resources > result[0]
		result[2] = maxThreshold - result[0];
		result[3] = (int) rand.nextDouble() * maxThreshold;
		return result;
	}
	
	public int getThresholdAt(int index)
	{
		return this.thresholds[index];
	}
	
	public void setThreshold(int index, int threshold)
	{
		this.thresholds[index] = threshold;
	}
	
	
}
