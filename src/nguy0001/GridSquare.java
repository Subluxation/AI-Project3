package nguy0001;

import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;

import java.awt.Color;
import java.util.ArrayList;

import spacesettlers.graphics.RectangleGraphics;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Ship;

public class GridSquare {
	double startX;
	public double endX;
	public double startY;
	public double endY;
	public Position center;
	
	double pathCost = 0.0;
	public boolean isEmpty = false;
	public boolean containsGoal = false;
	public boolean containsShip = false;
	boolean inside1 = false;
	boolean inside2 = false;
	
	public GridSquare(double startX, double endX, double startY, double endY)
	{
		this.startX = startX;
		this.endX = endX;
		
		this.startY = startY;
		this.endY = endY;
		
		this.center = new Position((endX + startX) / 2, (endY + startY) / 2);
	}
	/**
	 * For updateObstacles method in model client
	 * @param space
	 * @param goal
	 * @param ship
	 */
	public void calculatePathCost(Toroidal2DPhysics space, AbstractObject goal, Ship ship)
	{
		if (containsGoal)
			this.pathCost = 0.0;
		else
			this.pathCost = space.findShortestDistance(ship.getPosition(), center) + space.findShortestDistance(center, goal.getPosition());
		
	}
	
	
	public boolean containsGoal(Toroidal2DPhysics space, AbstractObject goal)
	{
		if (space.findShortestDistance(center, goal.getPosition()) <= space.getHeight()/25)
			containsGoal = true;
		else
			containsGoal = false;
		
		return containsGoal;
	}
	
	public boolean containsShip(Toroidal2DPhysics space, Ship ship)
	{
		if (space.findShortestDistance(center, ship.getPosition()) <= space.getHeight()/25)
			containsShip = true;
		else
			containsShip = false;
		
		return containsShip;
	}
	
//	public boolean isEmpty(Toroidal2DPhysics space)
//	{
//		if (space.isLocationFree(center, (int) ((endX - startX) / 2)))//space.getWidth()/50
//			this.isEmpty = true;
//		else
//			this.isEmpty = false;
//		return isEmpty;
//	}
	
	/**
	 * Test for whether or not the object is within the grid
	 * @param space
	 * @param object
	 * @return true if within, false if not.
	 */
	public boolean isWithin(Toroidal2DPhysics space, AbstractObject object) {
		double tempX = object.getPosition().getX();
		double tempY = object.getPosition().getY();
		// ***variance of 5 to try to "include" the objects center and radius***
		if(tempX - 5 >= startX && tempX - 5 <= endX) {
			if(tempY - 5 >= startY && tempY - 5 <= endY) {
				this.inside1 = true;
			}
		}
		else {
			this.inside1 = false;
		}
		if(tempX + 5 >= startX && tempX + 5 <= endX) {
			if(tempY + 5 >= startY && tempY + 5 <= endY) {
				this.inside2 = true;
			}
		}
		else {
			this.inside2 = false;
		}
		return (inside1 || inside2);
	}
	/**
	 * Checks all 8 locations around the main grid to see if this grid is adjacent to it
	 * @param space
	 * @param mainGrid
	 * @return true if adjacent, false is not.
	 */
	public boolean isAdjacent(GridSquare mainGrid) {
		System.out.println("Center (x,y): " + center.getX() + " " + center.getY() + "\nMainGrid (x,y): " + mainGrid.center.getX() + " " + mainGrid.center.getY());
		//to the EAST
		if(new Position(center.getX() + getWidth(),center.getY()).equals(mainGrid.center)) {
			System.out.println("EAST");
			return true;
		}
		//to the WEST
		if(new Position(center.getX() - getWidth(),center.getY()).equals(mainGrid.center)) {
			System.out.println("WEST");
			return true;
		}
		//to the NORTH
		if(new Position(center.getX(),center.getY() + getHeight()).equals(mainGrid.center)) {
			System.out.println("NORTH");
			return true;
		}
		//to the SOUTH
		if(new Position(center.getX(),center.getY() - getHeight()).equals(mainGrid.center)) {
			System.out.println("SOUTH");
			return true;
		}
		//to the NORTH-EAST
		if(new Position(center.getX() + getWidth(),center.getY() + getHeight()).equals(mainGrid.center)) {
			System.out.println("NORTH-EAST");
			return true;
		}
		//to the SOUTH-EAST
		if(new Position(center.getX() - getWidth(),center.getY() - getHeight()).equals(mainGrid.center)) {
			System.out.println("SOUTH-EAST");
			return true;
		}
		//to the SOUTH-WEST
		if(new Position(center.getX() - getWidth(),center.getY() - getHeight()).equals(mainGrid.center)) {
			System.out.println("SOUTH-WEST");
			return true;
		}
		//to the NORTH-WEST
		if(new Position(center.getX() - getWidth(),center.getY() + getHeight()).equals(mainGrid.center)) {
			System.out.println("NORTH-WEST");
			return true;
		}
		//if not adjacent, return false
		return false;
	}
	
	public RectangleGraphics getGraphics()
	{
		if (containsShip)
			return new RectangleGraphics((int) (endX - startX), (int) (endY - startY), Color.BLUE, new Position(startX, startY));
		else if (containsGoal)
			return new RectangleGraphics((int) (endX - startX), (int) (endY - startY), Color.YELLOW, new Position(startX, startY));
		else
			return new RectangleGraphics((int) (endX - startX), (int) (endY - startY), Color.RED, new Position(startX, startY));
	}
	/**
	 * Tests whether or not there is an object in the grid
	 * @param space
	 * @return isEmpty variable
	 */
	public boolean isEmpty(Toroidal2DPhysics space)
	{
		for(AbstractObject object : space.getAllObjects()) {
			if(isWithin(space,object)) {
				this.isEmpty = false;
				return isEmpty;
			}
		}
		this.isEmpty = true;
		return isEmpty;
		
	}
	/**
	 * 
	 * @param grid
	 * @return
	 */
	public int CompareTo(GridSquare grid) {
		if(this.pathCost > grid.pathCost) {
			return 1;
		}
		else if(this.pathCost < grid.pathCost){
			return -1;
		}
		else {
			return 0;
		}
		
	}
	/**
	 * Get the adjacent grids to the ship
	 * @param grid the whole grid of the space
	 * @return
	 */
	public static ArrayList<GridSquare> getAdjacent(ArrayList<ArrayList<GridSquare>> grid, GridSquare target)
	{
		ArrayList<GridSquare> adjacentGrids = new ArrayList<GridSquare>();
		int row = -1;
		int col = -1;
		
		// Finds the grid specifically containing the target
		for (int i = 0; i < grid.size(); i++)
		{
			for (int j = 0; j < grid.size(); j++)
			{
				// Gets the index of the grid containing the target
				if (grid.get(i).get(j).center == target.center)
				{
					row = i;
					col = j;
				}		
			}
		}
		
		// If the grid is not on any edges of the map
		if ((row > 0 && row < grid.size() - 1) && (col > 0 && col < grid.size() - 1))
		{
			// SW row + 1, col - 1
			adjacentGrids.add(grid.get(row + 1).get(col - 1));
			
			// S row + 1
			adjacentGrids.add(grid.get(row + 1).get(col));
			
			// SE row + 1, col + 1
			adjacentGrids.add(grid.get(row + 1).get(col + 1));
			
			// W col - 1
			adjacentGrids.add(grid.get(row).get(col - 1));
			
			// E col + 1
			adjacentGrids.add(grid.get(row).get(col + 1));
			
			// NW row - 1, col - 1
			adjacentGrids.add(grid.get(row - 1).get(col - 1));
			
			// N row - 1
			adjacentGrids.add(grid.get(row - 1).get(col));
			
			// NE row - 1, col + 1
			adjacentGrids.add(grid.get(row - 1).get(col + 1));
		}
		// If the grid is on the top edge of the map
		else if (row == 0)
		{
			// If the col is not on the left or right edge
			if (col > 0 && col < grid.size() - 1)
			{
				// SW row + 1, col - 1
				adjacentGrids.add(grid.get(row + 1).get(col - 1));
				
				// S row + 1
				adjacentGrids.add(grid.get(row + 1).get(col));
				
				// SE row + 1, col + 1
				adjacentGrids.add(grid.get(row + 1).get(col + 1));
				
				// W col - 1
				adjacentGrids.add(grid.get(row).get(col - 1));
				
				// E col + 1
				adjacentGrids.add(grid.get(row).get(col + 1));
				
				// NW row - 1, col - 1
				// add 25 to make it wrap around
				adjacentGrids.add(grid.get(row - 1 + grid.size()).get(col - 1));
				
				// N row - 1
				// add 25 to make it wrap around
				adjacentGrids.add(grid.get(row - 1 + grid.size()).get(col));
				
				// NE row - 1, col + 1
				// add 25 to make it wrap around
				adjacentGrids.add(grid.get(row - 1 + grid.size()).get(col + 1));
			}
			// If the grid is on the top and left corner of the map
			else if (col == 0)
			{
				// SW row + 1, col - 1
				// add 25 to make it wrap around
				adjacentGrids.add(grid.get(row + 1).get(col - 1 + grid.size()));
				
				// S row + 1
				adjacentGrids.add(grid.get(row + 1).get(col));
				
				// SE row + 1, col + 1
				adjacentGrids.add(grid.get(row + 1).get(col + 1));
				
				// W col - 1
				// add 25 to make it wrap around
				adjacentGrids.add(grid.get(row).get(col - 1 + grid.size()));
				
				// E col + 1
				adjacentGrids.add(grid.get(row).get(col + 1));
				
				// NW row - 1, col - 1
				// add 25 to make it wrap around
				adjacentGrids.add(grid.get(row - 1 + grid.size()).get(col - 1 + grid.size()));
				
				// N row - 1
				// add 25 to make it wrap around
				adjacentGrids.add(grid.get(row - 1 + grid.size()).get(col));
				
				// NE row - 1, col + 1
				// add 25 to make it wrap around
				adjacentGrids.add(grid.get(row - 1 + grid.size()).get(col + 1));
			}
			
			else if (col == grid.size() - 1)
			{
				// SW row + 1, col - 1
				adjacentGrids.add(grid.get(row + 1).get(col - 1));
				
				// S row + 1
				adjacentGrids.add(grid.get(row + 1).get(col));
				
				// SE row + 1, col + 1
				// subtract 25 to make it wrap around
				adjacentGrids.add(grid.get(row + 1).get(col + 1 - grid.size()));
				
				// W col - 1
				adjacentGrids.add(grid.get(row).get(col - 1));
				
				// E col + 1
				// subtract 25 to make it wrap around
				adjacentGrids.add(grid.get(row).get(col + 1 - grid.size()));
				
				// NW row - 1, col - 1
				// add 25 to make it wrap around
				adjacentGrids.add(grid.get(row - 1 + grid.size()).get(col - 1));
				
				// N row - 1
				// add 25 to make it wrap around
				adjacentGrids.add(grid.get(row - 1 + grid.size()).get(col));
				
				// NE row - 1, col + 1
				// add and subtract 25 to make it wrap around
				adjacentGrids.add(grid.get(row - 1 + grid.size()).get(col + 1 - grid.size()));
			}
			
		}
		// If the row is on the bottom edge
		else if (row == grid.size() - 1)
		{
			// If the col is not on the left or right edge
			if (col > 0 && col < grid.size() - 1)
			{
				// SW row + 1, col - 1
				// subtract 25 to make it wrap around
				adjacentGrids.add(grid.get(row + 1 - grid.size()).get(col - 1));
				
				// S row + 1
				// subtract 25 to make it wrap around
				adjacentGrids.add(grid.get(row + 1 - grid.size()).get(col));
				
				// SE row + 1, col + 1
				// subtract 25 to make it wrap around
				adjacentGrids.add(grid.get(row + 1 - grid.size()).get(col + 1));
				
				// W col - 1
				adjacentGrids.add(grid.get(row).get(col - 1));
				
				// E col + 1
				adjacentGrids.add(grid.get(row).get(col + 1));
				
				// NW row - 1, col - 1
				adjacentGrids.add(grid.get(row - 1).get(col - 1));
				
				// N row - 1
				adjacentGrids.add(grid.get(row - 1).get(col));
				
				// NE row - 1, col + 1
				adjacentGrids.add(grid.get(row - 1).get(col + 1));
			}
			// If the grid is on the bottom and left corner of the map
			else if (col == 0)
			{
				// SW row + 1, col - 1
				// add and subtract 25 to make it wrap around
				adjacentGrids.add(grid.get(row + 1 - grid.size()).get(col - 1 + grid.size()));
				
				// S row + 1
				// subtract 25 to make it wrap around
				adjacentGrids.add(grid.get(row + 1 - grid.size()).get(col));
				
				// SE row + 1, col + 1
				// subtract 25 to make it wrap around
				adjacentGrids.add(grid.get(row + 1 - grid.size()).get(col + 1));
				
				// W col - 1
				// add 25 to make it wrap around
				adjacentGrids.add(grid.get(row).get(col - 1 + grid.size()));
				
				// E col + 1
				adjacentGrids.add(grid.get(row).get(col + 1));
				
				// NW row - 1, col - 1
				adjacentGrids.add(grid.get(row - 1).get(col - 1 + grid.size()));
				
				// N row - 1
				adjacentGrids.add(grid.get(row - 1).get(col));
				
				// NE row - 1, col + 1
				adjacentGrids.add(grid.get(row - 1).get(col + 1));
			}
			// If the grid is on the bottom and right corner of the map
			else if (col == grid.size() - 1)
			{
				// SW row + 1, col - 1
				adjacentGrids.add(grid.get(row + 1 - grid.size()).get(col - 1));
				
				// S row + 1
				adjacentGrids.add(grid.get(row + 1 - grid.size()).get(col));
				
				// SE row + 1, col + 1
				// subtract 25 to make it wrap around
				adjacentGrids.add(grid.get(row + 1 - grid.size()).get(col + 1 - grid.size()));
				
				// W col - 1
				adjacentGrids.add(grid.get(row).get(col - 1));
				
				// E col + 1
				// subtract 25 to make it wrap around
				adjacentGrids.add(grid.get(row).get(col + 1 - grid.size()));
				
				// NW row - 1, col - 1
				// add 25 to make it wrap around
				adjacentGrids.add(grid.get(row - 1).get(col - 1));
				
				// N row - 1
				adjacentGrids.add(grid.get(row - 1).get(col));
				
				// NE row - 1, col + 1
				// subtract 25 to make it wrap around
				adjacentGrids.add(grid.get(row - 1).get(col + 1 - grid.size()));
			}
		}
		adjacentGrids.sort(new GridComparator());
//		System.out.println(adjacentGrids.size());
		ArrayList<GridSquare> finalList = new ArrayList<GridSquare>();
		for(int i = 0; i < adjacentGrids.size(); i++) {
			if(adjacentGrids.get(i).isEmpty) {
				finalList.add(adjacentGrids.get(i));
			}
		}
//		System.out.println(finalList.size());
		return finalList;
	}
	
	public double getWidth()
	{
		return endX - startX;
	}
	
	public double getHeight()
	{
		return endY - startY;
	}
	
	public double getPathCost()
	{
		return pathCost;
	}
}
