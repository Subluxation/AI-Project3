package nguy0001;

import java.util.ArrayList;

/**
 * Class for Fitness Function
 * Performance is on a scale of 0 - 15; 
 * 0-7: Bad
 * 8-15: Good
 * @author SpencerBarnes
 *
 */
public class FitnessFunction {
	//Object variables
	static int deaths;
	static double score;
	static int coresCollected;
	static int coresGoneAfter;
	static int baseReturns;
	static int performance;
	
	/**
	 * Empty constructor for the FitnessFunction Object
	 */
	public FitnessFunction() {
		deaths = 0;
		score = 0;
		coresCollected = 0;
		coresGoneAfter = 0;
		baseReturns = 0;
		performance = 0;
	}
	/**
	 * Rating the performance of the ship
	 * Deaths, score, & coreRatio
	 */
	public void ratePerformance() {
		double coreRatio = coresCollected / coresGoneAfter;
		
		//rate deaths
		switch(deaths) {
			case 0:
				performance += 5;
				break;
			case 1:
				performance += 4;
				break;
			case 2:
				performance += 3;
				break;
			case 3:
				performance += 2;
				break;
			case 4:
				performance += 1;
				break;
			default:
				break;
		}
		//rate cores collected per core gone after
		if(coreRatio > .9) {
			performance += 5;
		}
		else if (coreRatio > .7) {
			performance += 4;
		}
		else if (coreRatio > .5) {
			performance += 3;
		}
		else if (coreRatio > .3) {
			performance += 2;
		}
		else if (coreRatio > .1) {
			performance += 1;
		}
		
		//rate off of score
		if(score > 100000.0) {
			performance += 5;
		}
		else if(score > 70000.0) {
			performance += 4;
		}
		else if(score > 30000.0) {
			performance += 3;
		}
		else if(score > 15000.0) {
			performance += 2;
		}
		else if(score > 4000.0) {
			performance += 1;
		}
		
	}
	/**
	 * Did the Ship perform well?
	 * @return true --> yes; false --> no
	 * 
	 */
	public boolean PerformWell() {
		if(performance > 8) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	
	
	public int getDeaths() {
		return deaths;
	}
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public int getCoresCollected() {
		return coresCollected;
	}
	public void setCoresCollected(int coresCollected) {
		this.coresCollected = coresCollected;
	}
	public int getCoresGoneAfter() {
		return coresGoneAfter;
	}
	public void setTotalCoresIngame(int coresGoneAfter) {
		this.coresGoneAfter = coresGoneAfter;
	}
	public int getBaseReturns() {
		return baseReturns;
	}
	public void setBaseReturns(int baseReturns) {
		this.baseReturns = baseReturns;
	}
	
	
	

	
}
