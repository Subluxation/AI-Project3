package nguy0001;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import nguy0001.GridSquare;
import nguy0001.GridComparator;
import nguy0001.AStar;
import nguy0001.MoveAction;
import spacesettlers.actions.MoveToObjectAction;
import spacesettlers.actions.AbstractAction;
import spacesettlers.actions.DoNothingAction;
//import spacesettlers.actions.MoveAction;
import spacesettlers.actions.PurchaseCosts;
import spacesettlers.actions.PurchaseTypes;
import spacesettlers.clients.TeamClient;
import spacesettlers.graphics.RectangleGraphics;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Base;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Ship;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

/**
 * Modification of the aggressive heuristic asteroid collector to a team that
 * only has one ship. It tries to collect resources but it also tries to shoot
 * other ships if they are nearby.
 * 
 * Made changes to retreat to a nearby ally if low on energy
 * 
 * @author amy modified by Anthony and Spencer
 */
public class AnthonyModelTeamClient extends TeamClient {
	Beacon beacon;
	Boolean finishedAStar = false;
	HashMap<UUID, Ship> asteroidToShipMap;
	HashMap<UUID, Boolean> aimingForBase;
	Comparator<GridSquare> comparator = new GridComparator();
	// Priority Queue of GridSquares
	PriorityQueue<GridSquare> queue = new PriorityQueue<GridSquare>(Collections.reverseOrder());
	// Grid represented by a matrix of GridSquares
	// Queue and Grid both initialized in the initialize() method
	static ArrayList<ArrayList<GridSquare>> grid;
	ArrayList<GridSquare> adjacentGrids = new ArrayList<GridSquare>();
	//	GridSquare grid;
	UUID asteroidCollectorID;
	double weaponsProbability = 1;
	boolean shouldShoot = false;
	AbstractObject goal;
	int threatZone = 200;



	/**
	 * Assigns ships to asteroids and beacons, as described above
	 */
	public Map<UUID, AbstractAction> getMovementStart(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, AbstractAction> actions = new HashMap<UUID, AbstractAction>();

		// loop through each ship
		for (AbstractObject actionable : actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;

				// the first time we initialize, decide which ship is the asteroid collector
				if (asteroidCollectorID == null) {
					asteroidCollectorID = ship.getId();
				}

				AbstractAction action = getAggressiveAsteroidCollectorAction(space, ship);
				actions.put(ship.getId(), action);

			} else {
				// it is a base. Heuristically decide when to use the shield (TODO)
				actions.put(actionable.getId(), new DoNothingAction());
			}
		}

		return actions;
	}

	/**
	 * Gets the action for the asteroid collecting ship (while being aggressive
	 * towards the other ships)
	 * 
	 * @param space
	 * @param ship
	 * @return
	 */
	private AbstractAction getAggressiveAsteroidCollectorAction(Toroidal2DPhysics space, Ship ship) {
		AbstractAction current = ship.getCurrentAction();
		Position currentPosition = ship.getPosition();



		//update the grid every 15 timesteps
		if (space.getCurrentTimestep() % 15 == 0)
		{
			updateObstacles(space, ship, goal);
		}

		//run aStar method every 75 timesteps
		if(space.getCurrentTimestep() % 75 == 0 || space.getCurrentTimestep() == 1) {
			//Pick nearest beacon and select it as goal
			beacon = pickNearestBeacon(space, ship);
			shouldShoot = false;
			aimingForBase.put(ship.getId(), false);
			this.goal = beacon;
			aStarMethod(space, goal,ship);
			
			//if the queue is not empty, pop the largest pathcost grid and go there
			if(queue.peek() != null) {
				System.out.println("Going to Location: " + queue.peek().center.toString());
				return new MoveAction(space,ship.getPosition(),queue.poll().center);
			}
			else {
				//bad grids are surrounding ship, relocate at timespace
				System.out.println("Bad Grids are all around Ship!");
				
//				beacon = pickNearestBeacon(space, ship);
//				shouldShoot = false;
//				aimingForBase.put(ship.getId(), false);
//				this.goal = beacon;
//				aStarMethod(space, goal,ship);
//				return ship.getCurrentAction();
				return new MoveToObjectAction(space,ship.getPosition(),(AbstractObject)pickNearestFreeAsteroid(space, ship));
			}




		}
//		if(space.getCurrentTimestep() == 0 || ship.getCurrentAction().isMovementFinished(space)) { 
//			//If the queue is empty, then search for new Beacon
//			if(!queue.isEmpty()) {
//				System.out.println("Going to Location: " + queue.peek().center.toString());
//				return new MoveAction(space,ship.getPosition(),queue.poll().center);
//			}
//			else {
//				System.out.println("Queue is Empty:");
//				beacon = pickNearestBeacon(space, ship);
//				shouldShoot = false;
//				aimingForBase.put(ship.getId(), false);
//				this.goal = beacon;
//				aStarMethod(space, goal,ship);
//				return new DoNothingAction();
//			}
//		}
		return new DoNothingAction();


		// Asteroid badAst = pickNearestUselessAsteroid(space, ship);

		// aim for a beacon if there isn't enough energy
		//		if (ship.getEnergy() > 2000) {

		//		}

		// -------------------------------------------------
		// if the ship has resources but energy is low
		//
		// purpose to get score without risk of getting killed
		// -------------------------------------------------
		//		if (ship.getResources().getTotal() > 500 && ship.getEnergy() < 2000) {
		//			Base base = findNearestBase(space, ship);
		//			aimingForBase.put(ship.getId(), true);
		//			shouldShoot = false;
		//			return goBackToNearestBase(space, ship, base);
		//		}
		//
		//		// if the ship has enough resourcesAvailable, take it back to base
		//		if (ship.getResources().getTotal() > 1000) {
		//			Base base = findNearestBase(space, ship);
		//			aimingForBase.put(ship.getId(), true);
		//			shouldShoot = false;
		//			return goBackToNearestBase(space, ship, base);
		//		}

		// did we bounce off the base?
		//		if (ship.getResources().getTotal() == 0 && ship.getEnergy() > 2000 && aimingForBase.containsKey(ship.getId())
		//				&& aimingForBase.get(ship.getId())) {
		//			current = null;
		//			aimingForBase.put(ship.getId(), false);
		//			shouldShoot = false;
		//		}

		// otherwise either for an asteroid or an enemy ship (depending on who is closer
		// and what we need)
		//		if (current == null || current.isMovementFinished(space)) {
		//			aimingForBase.put(ship.getId(), false);
		//
		//			// see if there is an enemy ship nearby
		//			Ship enemy = pickNearestEnemyShip(space, ship);
		//
		//			// find the highest valued nearby asteroid
		//			Asteroid asteroid = pickNearestFreeAsteroid(space, ship);
		//
		//			AbstractAction newAction = null;
		//
		//			// if there is no enemy nearby, go for an asteroid
		//			if (enemy == null) {
		//				if (asteroid != null) {
		//					return mineAsteroid(space, ship, asteroid);
		//				} else {
		//					// no enemy and no asteroid, just skip this turn (shouldn't happen often)
		//					shouldShoot = true;
		//					newAction = new DoNothingAction();
		//					return newAction;
		//				}
		//			}
		//
		//			// now decide which one to aim for
		//			if (asteroid != null) {
		//				double enemyDistance = space.findShortestDistance(ship.getPosition(), enemy.getPosition());
		//				double asteroidDistance = space.findShortestDistance(ship.getPosition(), asteroid.getPosition());
		//
		//				// we are aggressive, so aim for enemies if they are nearby
		//				// --- and if they have any resources ---
		//				if (enemyDistance < asteroidDistance && enemy.getResources().getTotal() > 0) {
		//					shouldShoot = true;
		//					newAction = new MoveToObjectAction(space, currentPosition, enemy,
		//							enemy.getPosition().getTranslationalVelocity());
		//
		//				} else {
		//					shouldShoot = false;
		//					newAction = mineAsteroid(space, ship, asteroid);
		//				}
		//				return newAction;
		//			} else {
		//				newAction = new MoveToObjectAction(space, currentPosition, enemy,
		//						enemy.getPosition().getTranslationalVelocity());
		//			}
		//			return newAction;
		//		}

		//		return ship.getCurrentAction();

	}

	public void avoidAsteroid(Toroidal2DPhysics space, Ship object1, Asteroid object2) {
		Vector2D distanceVec = space.findShortestDistanceVector(object1.getPosition(), object2.getPosition());
		Vector2D unitNormal = distanceVec.getUnitVector();
		Vector2D unitTangent = new Vector2D(-unitNormal.getYValue(), unitNormal.getXValue());

		double m1 = object1.getMass();
		double m2 = object2.getMass();

		// get the velocity vectors
		Vector2D velocity1 = object1.getPosition().getTranslationalVelocity();
		Vector2D velocity2 = object2.getPosition().getTranslationalVelocity();

		// get the scalars in each direction
		double u1 = velocity1.dot(unitNormal);
		double u2 = velocity2.dot(unitNormal);
		double t1 = velocity1.dot(unitTangent);

		double v1 = ((u1 * (m1 - m2)) + (2 * m2 * u2)) / (m1 + m2);
		// now get it back to the original space
		Vector2D vel1Normal = unitNormal.multiply(v1);
		Vector2D vel1Tangent = unitTangent.multiply(t1);

		// add the normal and tangential parts
		Vector2D newVelocity1 = vel1Normal.add(vel1Tangent);

		object1.getPosition().setTranslationalVelocity(newVelocity1);
	}
	/*
	 * 
	 * Goes back to base, if there are any asteroids in the path, take a new path
	 * 
	 */

	public AbstractAction goBackToNearestBase(Toroidal2DPhysics space, Ship ship, Base base) {
		AbstractAction newAction = new MoveToObjectAction(space, ship.getPosition(), base,
				ship.getPosition().getTranslationalVelocity());
		return newAction;
	}

	/*
	 * 
	 * Retrieves the chosen Beacon
	 * 
	 */
	public AbstractAction retrieveBeacon(Toroidal2DPhysics space, Ship ship, Beacon beacon) {
		AbstractAction newAction = null;
		newAction = new MoveToObjectAction(space, ship.getPosition(), beacon);
		return newAction;
	}

	/*
	 * 
	 * Checks if the path to the mineable asteroid is clear of other non-mineable
	 * asteroids Takes a rotated path if it isn't
	 * 
	 */
	public AbstractAction mineAsteroid(Toroidal2DPhysics space, Ship ship, Asteroid asteroid) {
		AbstractAction newAction = null;
		newAction = new MoveToObjectAction(space, ship.getPosition(), asteroid,
				ship.getPosition().getTranslationalVelocity());
		return newAction;
	}

	public boolean isPathClearOfAsteroids(Toroidal2DPhysics space, Position startPosition, Position goalPosition,
			Set<Asteroid> obstructions, int freeRadius) {
		Vector2D pathToGoal = space.findShortestDistanceVector(startPosition, goalPosition); // Shortest straight line
		// path from
		// startPosition to
		// goalPosition
		double distanceToGoal = pathToGoal.getMagnitude(); // Distance of straight line path

		boolean pathIsClear = true; // Boolean showing whether or not the path is clear

		// Calculate distance between obstruction center and path (including buffer for
		// ship movement)
		// Uses hypotenuse * sin(theta) = opposite (on a right hand triangle)
		Vector2D pathToObstruction; // Vector from start position to obstruction
		double angleBetween; // Angle between vector from start position to obstruction

		// Loop through obstructions
		for (Asteroid obstruction : obstructions) {
			if (!obstruction.isMineable()) {
				// If the distance to the obstruction is greater than the distance to the end
				// goal, ignore the obstruction
				pathToObstruction = space.findShortestDistanceVector(startPosition, obstruction.getPosition());
				if (pathToObstruction.getMagnitude() > distanceToGoal) {
					continue;
				}

				// Ignore angles > 90 degrees
				angleBetween = Math.abs(pathToObstruction.angleBetween(pathToGoal));
				if (angleBetween > Math.PI / 2) {
					continue;
				}

				// Compare distance between obstruction and path with buffer distance
				if (pathToObstruction.getMagnitude() * Math.sin(angleBetween) < obstruction.getRadius()
						+ freeRadius * 1.5) {
					pathIsClear = false;
					break;
				}
			}
		}
		return pathIsClear;

	}

	/**
	 * Returns the asteroid of no value
	 * 
	 * potentially avoid asteroids instead of running into them
	 * 
	 * @return
	 */
	private Asteroid pickNearestUselessAsteroid(Toroidal2DPhysics space, Ship ship) {
		Set<Asteroid> asteroids = space.getAsteroids();
		Asteroid bestAsteroid = null;
		double minDistance = Double.POSITIVE_INFINITY;

		for (Asteroid asteroid : asteroids) {
			if (!asteroid.isMineable()) {
				double dist = space.findShortestDistance(asteroid.getPosition(), ship.getPosition());
				if (dist < minDistance) {
					// System.out.println("Considering asteroid " + asteroid.getId() + " as a best
					// one");
					bestAsteroid = asteroid;
				}
			}
		}
		// System.out.println("Best asteroid has " + bestMoney);
		return bestAsteroid;
	}

	/**
	 * Find the nearest ship on our team and aim for it Goal is to potentially
	 * retreat to ally ship
	 * 
	 * @param space
	 * @param ship
	 * @return
	 */
	private Ship pickNearestFriendlyShip(Toroidal2DPhysics space, Ship ship) {
		double minDistance = Double.POSITIVE_INFINITY;
		Ship nearestShip = null;
		for (Ship otherShip : space.getShips()) {
			// aim for our own team
			if (otherShip.getTeamName().equals(ship.getTeamName())) {
				double distance = space.findShortestDistance(ship.getPosition(), otherShip.getPosition());
				if (distance < minDistance) {
					minDistance = distance;
					nearestShip = otherShip;
				}
			}
		}

		return nearestShip;
	}

	/**
	 * Find the nearest ship on another team and aim for it
	 * 
	 * @param space
	 * @param ship
	 * @return
	 */
	private Ship pickNearestEnemyShip(Toroidal2DPhysics space, Ship ship) {
		double minDistance = Double.POSITIVE_INFINITY;
		Ship nearestShip = null;
		for (Ship otherShip : space.getShips()) {
			// don't aim for our own team (or ourself)
			if (otherShip.getTeamName().equals(ship.getTeamName())) {
				continue;
			}

			double distance = space.findShortestDistance(ship.getPosition(), otherShip.getPosition());
			if (distance < minDistance) {
				minDistance = distance;
				nearestShip = otherShip;
			}
		}

		return nearestShip;
	}

	/**
	 * Find the base for this team nearest to this ship
	 * 
	 * @param space
	 * @param ship
	 * @return
	 */
	private Base findNearestBase(Toroidal2DPhysics space, Ship ship) {
		double minDistance = Double.MAX_VALUE;
		Base nearestBase = null;

		for (Base base : space.getBases()) {
			if (base.getTeamName().equalsIgnoreCase(ship.getTeamName())) {
				double dist = space.findShortestDistance(ship.getPosition(), base.getPosition());
				if (dist < minDistance) {
					minDistance = dist;
					nearestBase = base;
				}
			}
		}
		return nearestBase;
	}

	/**
	 * Returns the closest asteroid of any value that isn't already being chased by
	 * this team
	 * 
	 * @return
	 */
	private Asteroid pickNearestFreeAsteroid(Toroidal2DPhysics space, Ship ship) {
		Set<Asteroid> asteroids = space.getAsteroids();
		Asteroid bestAsteroid = null;
		double minDistance = Double.MAX_VALUE;

		for (Asteroid asteroid : asteroids) {
			if (!asteroidToShipMap.containsKey(asteroid.getId())) {
				if (asteroid.isMineable()) {
					double dist = space.findShortestDistance(asteroid.getPosition(), ship.getPosition());
					if (dist < minDistance) {
						// System.out.println("Considering asteroid " + asteroid.getId() + " as a best
						// one");
						bestAsteroid = asteroid;
						minDistance = dist;
					}
				}
			}
		}
		// System.out.println("Best asteroid has " + bestMoney);
		return bestAsteroid;
	}

	/**
	 * Find the nearest beacon to this ship
	 * 
	 * @param space
	 * @param ship
	 * @return
	 */
	private Beacon pickNearestBeacon(Toroidal2DPhysics space, Ship ship) {
		// get the current beacons
		Set<Beacon> beacons = space.getBeacons();

		Beacon closestBeacon = null;
		double bestDistance = Double.POSITIVE_INFINITY;

		for (Beacon beacon : beacons) {
			double dist = space.findShortestDistance(ship.getPosition(), beacon.getPosition());
			if (dist < bestDistance) {
				bestDistance = dist;
				closestBeacon = beacon;
			}
		}

		return closestBeacon;
	}

	@Override
	public void getMovementEnd(Toroidal2DPhysics space, Set<AbstractActionableObject> actionableObjects) {
		ArrayList<Asteroid> finishedAsteroids = new ArrayList<Asteroid>();

		for (UUID asteroidId : asteroidToShipMap.keySet()) {
			Asteroid asteroid = (Asteroid) space.getObjectById(asteroidId);
			if (asteroid == null || !asteroid.isAlive() || asteroid.isMoveable()) {
				finishedAsteroids.add(asteroid);
				// System.out.println("Removing asteroid from map");
			}
		}

		for (Asteroid asteroid : finishedAsteroids) {
			asteroidToShipMap.remove(asteroid.getId());
		}

	}

	@Override
	public void initialize(Toroidal2DPhysics space) {
		asteroidToShipMap = new HashMap<UUID, Ship>();
		asteroidCollectorID = null;
		aimingForBase = new HashMap<UUID, Boolean>();
		grid = new ArrayList<ArrayList<GridSquare>>();
		//queue = new PriorityQueue<GridSquare>(new GridComparator());
		// Width and HeightGrids hold the respective sizes of each grid
		// Where each grid square has a width of 1/25 of the map's width
		// and likewise for its height
		double widthGrid = space.getWidth()/20;
		double heightGrid = space.getHeight()/20;
		// temp used to add in the Grid matrix
		ArrayList<GridSquare> temp = new ArrayList<GridSquare>();
		// First grid square at 0,0
		temp.add(new GridSquare(0, widthGrid, 0, heightGrid));
		// First row of gridSquares**CHANGED TO i = 0
		for (int i = 1; i < 20; i++)
		{
			temp.add(new GridSquare(temp.get(i - 1).endX, temp.get(i - 1).endX + widthGrid, temp.get(i - 1).startY, heightGrid));
		}
		grid.add(temp);
		// Rest of the rows
		// Represents the rows
		for (int i = 1; i < 20; i++)
		{
			temp = new ArrayList<GridSquare>();
			// For the first square of the row, it starts on the very left, and then uses the previous row's endY to determine its startY
			temp.add(new GridSquare(0, widthGrid, grid.get(i - 1).get(0).endY, grid.get(i - 1).get(0).endY + heightGrid));
			// Represents the columns
			for (int j = 1; j < 20; j++)
			{
				// For the next squares in the row, it uses the previous squares in its own row to determine its startX
				temp.add(new GridSquare(temp.get(j - 1).endX, temp.get(j - 1).endX + widthGrid, grid.get(i - 1).get(0).endY, grid.get(i - 1).get(0).endY + heightGrid));
			}
			grid.add(temp);
		}
	}

	@Override
	public void shutDown(Toroidal2DPhysics space) {
		// TODO Auto-generated method stub

	}

	/**
	 * A* method for searching
	 * @param space
	 */
	public void aStarMethod(Toroidal2DPhysics space, AbstractObject goal,Ship ship) {
		GridSquare shipGrid = null;
		//reversed queue, highest values go to first
		//queue = new PriorityQueue<>(Collections.reverseOrder());
		
		//calculating path cost for each
		for(int i = 0; i < grid.size(); i++) {
			for(int j = 0; j < grid.get(i).size(); j++) {
				grid.get(i).get(j).calculatePathCost(space, goal, ship);
			}
		}


		//Finds the ships grid
		outerloop:
			for(int i = 0; i < grid.size(); i++) {
				for(int j = 0; j < grid.get(i).size(); j++) {
					if(grid.get(i).get(j).containsShip) {
						System.out.println("FOUND SHIP GRID");
						shipGrid = grid.get(i).get(j);
						break outerloop;
					}
				}
			}
		if(shipGrid != null) {
			queue = AStar.finalAStarMethod(AStar.getAdjacentTree(GridSquare.getAdjacent(grid, shipGrid)),GridSquare.getAdjacent(grid, shipGrid));
		}
		else {
			System.err.println("Error: shipGrid is null");
		}


	}
	/**
	 * Find all adjacent GridSquare's to the middle grid, excluding non-empty grids.
	 * @param middleGrid
	 * @return An ArrayList of the Adjacent Grid's
	 */
	public ArrayList<GridSquare> getAdjacentGrids(GridSquare middleGrid){
		ArrayList<GridSquare> adjacentGridList = new ArrayList<GridSquare>();
		for(int i = 0; i < grid.size() - 1; i++) {
			for(int j = 0; j < grid.get(i).size() - 1; j++) {
				if(grid.get(i).get(j).isAdjacent(middleGrid)) {
					if(grid.get(i).get(j).isEmpty || grid.get(i).get(j).containsGoal) {
						adjacentGridList.add(grid.get(i).get(j));
					}
				}
			}
		}
		return adjacentGridList;
	}


	public void updateObstacles(Toroidal2DPhysics space, Ship ship, AbstractObject goal)
	{
		for (ArrayList<GridSquare> grids: grid)
		{
			for (GridSquare square: grids)
			{
				square.isEmpty(space);
				square.containsShip(space, ship);
				if (goal != null)
					square.containsGoal(space, goal);	
				//TODO: REMOVED not sure if needed --> square.calculatePathCost(space, goal, ship);
			}
		}
	}

	@Override
	public Set<SpacewarGraphics> getGraphics() {
		// TODO Auto-generated method stub
		Set<SpacewarGraphics> graphics = new LinkedHashSet<SpacewarGraphics>();
		for (ArrayList<GridSquare> grids: grid)
		{
			for (GridSquare square: grids)
			{
				if (square.containsGoal || square.containsShip)
				{
					RectangleGraphics rect = square.getGraphics();
					//					rect.setFill(true);
					graphics.add(rect);
					//					if (square.containsShip)
					//						System.out.println(square.getPathCost());
				}
				else if (!square.isEmpty)
				{
					RectangleGraphics rect = square.getGraphics();
					rect.setFill(true);
					graphics.add(rect);
				}
				else
					graphics.add(square.getGraphics());
				//				else if (!square.isEmpty && (square.containsShip || square.containsGoal))
				//				{
				//					graphics.add(square.getGraphics());
				//				}
				//				else
				//					graphics.add(square.getGraphics());
			}
		}
		return graphics;
	}

	@Override
	/**
	 * If there is enough resourcesAvailable, buy a base. Place it by finding a ship
	 * that is sufficiently far away from the existing bases
	 */
	public Map<UUID, PurchaseTypes> getTeamPurchases(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects, ResourcePile resourcesAvailable,
			PurchaseCosts purchaseCosts) {

		HashMap<UUID, PurchaseTypes> purchases = new HashMap<UUID, PurchaseTypes>();
		double BASE_BUYING_DISTANCE = 200;
		boolean bought_base = false;

		if (purchaseCosts.canAfford(PurchaseTypes.BASE, resourcesAvailable)) {
			for (AbstractActionableObject actionableObject : actionableObjects) {
				if (actionableObject instanceof Ship) {
					Ship ship = (Ship) actionableObject;
					Set<Base> bases = space.getBases();

					// how far away is this ship to a base of my team?
					double maxDistance = Double.MIN_VALUE;
					for (Base base : bases) {
						if (base.getTeamName().equalsIgnoreCase(getTeamName())) {
							double distance = space.findShortestDistance(ship.getPosition(), base.getPosition());
							if (distance > maxDistance) {
								maxDistance = distance;
							}
						}
					}

					if (maxDistance > BASE_BUYING_DISTANCE) {
						purchases.put(ship.getId(), PurchaseTypes.BASE);
						bought_base = true;
						// System.out.println("Buying a base!!");
						break;
					}
				}
			}
		}

		// see if you can buy EMPs
		if (purchaseCosts.canAfford(PurchaseTypes.POWERUP_EMP_LAUNCHER, resourcesAvailable)) {
			for (AbstractActionableObject actionableObject : actionableObjects) {
				if (actionableObject instanceof Ship) {
					Ship ship = (Ship) actionableObject;

					if (!ship.getId().equals(asteroidCollectorID)
							&& !ship.isValidPowerup(PurchaseTypes.POWERUP_EMP_LAUNCHER.getPowerupMap())) {
						purchases.put(ship.getId(), PurchaseTypes.POWERUP_EMP_LAUNCHER);
					}
				}
			}
		}

		// can I buy a ship?
		// Commented out, don't buy a ship
		// if (purchaseCosts.canAfford(PurchaseTypes.SHIP, resourcesAvailable) &&
		// bought_base == false) {
		// for (AbstractActionableObject actionableObject : actionableObjects) {
		// if (actionableObject instanceof Base) {
		// Base base = (Base) actionableObject;
		//
		// purchases.put(base.getId(), PurchaseTypes.SHIP);
		// break;
		// }
		//
		// }
		//
		// }

		return purchases;
	}

	/**
	 * The aggressive asteroid collector shoots if there is an enemy nearby!
	 * 
	 * @param space
	 * @param actionableObjects
	 * @return
	 */
	@Override
	public Map<UUID, SpaceSettlersPowerupEnum> getPowerups(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, SpaceSettlersPowerupEnum> powerUps = new HashMap<UUID, SpaceSettlersPowerupEnum>();

		for (AbstractActionableObject actionableObject : actionableObjects) {
			SpaceSettlersPowerupEnum powerup = SpaceSettlersPowerupEnum.values()[random
			                                                                     .nextInt(SpaceSettlersPowerupEnum.values().length)];
			if (actionableObject.isValidPowerup(powerup) && random.nextDouble() < weaponsProbability && shouldShoot) {
				powerUps.put(actionableObject.getId(), powerup);
			}
		}

		return powerUps;
	}

}
