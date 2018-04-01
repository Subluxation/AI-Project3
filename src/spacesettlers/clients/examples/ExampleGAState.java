package spacesettlers.clients.examples;

import java.util.Set;

import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

/**
 * Example state representation for a GA agent.  Note this would need to be significantly 
 * modified/augmented to work in the full case (it just looks at one aspect of the 
 * information available to it).  Note also that this representation ignores which ship
 * it is on the team and just looks for the nearest asteroid.
 *  
 * @author amy
 *
 */
public class ExampleGAState {
	double distanceToNearestMineableAsteroid;
	Asteroid nearestMineableAsteroid;
	boolean isFindingAsteroid;
	double distanceToNearestAbstractObject;
	Beacon nearestBeacon;
	boolean isFindingBeacon;
	boolean isFindingBase;
	boolean isFindingCore;
	

	public ExampleGAState(Toroidal2DPhysics space, Ship myShip) {
		updateState(space, myShip);
	}


	/**
	 * Update the distance to the nearest mineable asteroid
	 * 
	 * @param space
	 * @param myShip
	 */
	public void updateState(Toroidal2DPhysics space, Ship myShip) {
		Set<Asteroid> asteroids = space.getAsteroids();
		Set<Beacon> beacons = space.getBeacons();
		
		distanceToNearestMineableAsteroid = Integer.MAX_VALUE;
		distanceToNearestAbstractObject = Integer.MAX_VALUE;
		double distance;

		for (Asteroid asteroid : asteroids) {
			if (asteroid.isMineable()) {
				distance = space.findShortestDistance(myShip.getPosition(), asteroid.getPosition());
				if (distance < distanceToNearestMineableAsteroid) {
					distanceToNearestMineableAsteroid = distance;
					nearestMineableAsteroid = asteroid;
				}
			}
		}
		
		for (Beacon beacon: beacons) {
			distance = space.findShortestDistance(myShip.getPosition(), beacon.getPosition());
			if (distance < distanceToNearestAbstractObject)
			{
				distanceToNearestAbstractObject = distance;
				nearestBeacon = beacon;
			}
		}
	}

	
	/**
	 * Return the nearest asteroid (used for actions)
	 * 
	 * @return
	 */
	public Asteroid getNearestMineableAsteroid() {
		return nearestMineableAsteroid;
	}


	/**
	 * Generated by eclipse - make sure you update this when you update the state (just use eclipse to regenerate it)
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(distanceToNearestMineableAsteroid);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}


	/**
	 * Generated by eclipse - make sure you update this when you update the state (just use eclipse to regenerate it)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExampleGAState other = (ExampleGAState) obj;
		if (Double.doubleToLongBits(distanceToNearestMineableAsteroid) != Double
				.doubleToLongBits(other.distanceToNearestMineableAsteroid))
			return false;
		return true;
	}



}
