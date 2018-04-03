package spacesettlers.clients.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import nguy0001.FitnessFunction;
import spacesettlers.simulator.Toroidal2DPhysics;

/**
 * Stores a whole population of individuals for genetic algorithms / evolutionary computation
 * 
 * @author amy
 *
 */
public class ExampleGAPopulation {
	private ExampleGAChromosome[] population;
	
	private int currentPopulationCounter;
	
	private double[] fitnessScores;

	/**
	 * Make a new empty population
	 */
	public ExampleGAPopulation(int populationSize) {
		super();
		
		// start at member zero
		currentPopulationCounter = 0;
		
		// make an empty population
		population = new ExampleGAChromosome[populationSize];
		
		for (int i = 0; i < populationSize; i++) {
			population[i] = new ExampleGAChromosome();
		}
		
		// make space for the fitness scores
		fitnessScores = new double[populationSize];
	}

	/**
	 * Currently scores all members as zero (the student must implement this!)
	 * 
	 * @param space
	 */
	public void evaluateFitnessForCurrentMember(Toroidal2DPhysics space) {
		FitnessFunction fitnessFn = new FitnessFunction();
		fitnessFn.ratePerformance();
		fitnessScores[currentPopulationCounter] = fitnessFn.getPerformance();
//		fitnessScores[currentPopulationCounter] = 0;
	}

	/**
	 * Return true if we have reached the end of this generation and false otherwise
	 * 
	 * @return
	 */
	public boolean isGenerationFinished() {
		if (currentPopulationCounter == population.length) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return the next member of the population (handles wrapping around by going
	 * back to the start but then the assumption is that you will reset with crossover/selection/mutation
	 * 
	 * @return
	 */
	public ExampleGAChromosome getNextMember() {
		currentPopulationCounter++;
		
		return population[currentPopulationCounter % population.length];
	}

	/**
	 * Does crossover, selection, and mutation using our current population.
	 * Note, none of this is implemented as it is up to the student to implement it.
	 * Right now all it does is reset the counter to the start.
	 */
	public void makeNextGeneration() {
//		currentPopulationCounter = 0;
		// Selection - Elitism
		double[] fitnessCopy = fitnessScores.clone();
		
		Arrays.sort(fitnessCopy);
		
		Random rand = new Random();
		int numSelection = (int) rand.nextDouble() * population.length/2;
		ExampleGAChromosome[] parents = new ExampleGAChromosome[numSelection];
		ExampleGAChromosome[] children = new ExampleGAChromosome[population.length];
		
		// Initializing the parents
		if (numSelection == 0)
		{
			// If the random generator gets 0, we need 1
			// the child will be the chromosome at the index of the largest fitness
			// which will be fitnessCopy[fitnessCopy.length - 1] since it is sorted by ascending
			parents[0] = population[Arrays.binarySearch(fitnessScores, fitnessCopy[fitnessCopy.length - 1])];
		}
		else
		{
			// Same algorithm as above
			// The next generation will inherit the top numSelection amount of chromosomes
			for (int i = 0; i < numSelection; i++)
			{
				parents[i] = population[Arrays.binarySearch(fitnessScores, fitnessCopy[fitnessCopy.length - 1 - i])];
			}
		}
		
		// Crossover
		for (int i = 0; i < parents.length; i++)
		{
			int splitIndex = (int) rand.nextDouble() * 3;
			// Makes sure the parent gets 1 trait to be passed
			while (splitIndex == 0)
			{
				splitIndex = (int) rand.nextDouble() * 3;
			}
			
			int randParent = (int) rand.nextDouble() * parents.length;
			// Makes sure the mate is not the same chromosome
			while (randParent == i)
			{
				randParent = (int) rand.nextDouble() * parents.length;
			}
			
			// Initially copies the parent over as a child
			children[i] = parents[i];
			// Mates the parent with a mate, changing the child at the splitIndex 
			// Perform single point crossover
			for (int j = splitIndex; j < 4; j++)
			{
				children[i].setThreshold(j, parents[randParent].getThresholdAt(j));
			}
			
			// Mutation
			for (int j = 0; j < 4; j++)
			{
				// Mutation with 10% chance
				if (rand.nextDouble() <= 0.1)
				{
					children[i].setThreshold(j, (int) rand.nextDouble() * 5000);
				}
			}
			

		}
		

	}

	/**
	 * Return the first member of the population
	 * @return
	 */
	public ExampleGAChromosome getFirstMember() {
		return population[0];
	}
}
