package nguy0001;

/**
 * Class for Fitness Function
 * @author SpencerBarnes
 *
 */
public class FitnessFunction {
	//Refers to overall performance, -1 for bad performance, +1 for good performance
	static double status = 0;
	public static final int SHIP_MAX_ENERGY = 5000;
	//Adjustable value for determining a good amount of energy to spend
	public static final int performanceVal = 650;
	//Adjustable value for determining a good fitness function
	public static final int goodPerformance = 10;
	
	
	
	public static void fitnessFunction(double startEnergy, double endEnergy, boolean accomplishedGoal) {
		
		//If energy spend > than allowed spending?
		if((startEnergy - endEnergy) > performanceVal ) {
			--status;
			//TODO:Work on spending less energy method here
		}
		else {
			++status;
			//TODO:Save energy spending method here(?)
		}
		//Did we accomplish the goal? i.e did we collect beacon?, energy?, attack ship?, etc
		if(accomplishedGoal == true) {
			++status;
			//TODO:Save method for completing goal here
		}
		else {
			--status;
			//TODO:Change method for completing goal here
		}
		
	}
	/**
	 * Checking to see if the Fitness function is performing well
	 * @return
	 */
	public static boolean isFunctionGood() {
		if(status >= goodPerformance) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	
	

	
}
