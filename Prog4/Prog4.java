// Programming Assignment 4: Prog4
// Author: John Potocny
// LoginID: poto4766
// Class: CS-203
// Professor: Jim Huggins
package Prog4;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Prog4
{
	// Function: main
	// Inputs: String array args
	// Returns: None
	// The main entry point for the application.
	public static void main(String[] args)
	{
		// Get the fileName that the user specified on the command line
		String fileName = (args.length == 1) ? args[0] : null;
		if (fileName == null)
		{
			// If no file was specified, exit
			System.out.println("Usage: Prog4 [filePath]");
			System.exit(-2);
		}

		// Our input file
		File inputFile = new File(fileName);
		// Check if the user's input file exists before using it
		if (!inputFile.exists())
		{
			System.out.printf("User input file %s not found.", inputFile);
			System.exit(-1);
		}

		// The cost matrix from the user's input file
		int[][] costMatrix = null;
		try
		{
			costMatrix = parseInputFile(inputFile);
		}
		catch (FileNotFoundException notFound)
		{
			notFound.printStackTrace();
		}

		if (costMatrix == null)
		{
			System.out.println("Parse input failed. Check to make "
					+ "sure file is not empty and is correctly formatted.");
			System.exit(-1);
		}

		// The optimal cost matrix that shows the minimal cost for each path
		int[][] optimalCost = optimalCost(costMatrix, costMatrix.length);
		reportOptimalCostMatrix(optimalCost, costMatrix, optimalCost.length);
	}

	// Function: parseInputFile
	// Inputs: File inputFile
	// Returns: Two dimensional array representing the travel cost matrix
	// parseInputFile will take the user's input file and return a costMatrix
	// representing the travel costs from each post to each end post
	public static int[][] parseInputFile(File inputFile) throws FileNotFoundException
	{
		// Our scanner to read the user's input file
		Scanner scan = new Scanner(inputFile);
		// The number of posts that the user has defined in their input file
		int numStations = scan.hasNextInt() ? scan.nextInt() : -1;
		scan.nextLine();
		if (numStations == -1)
		{
			scan.close();
			return null;
		}

		// The cost matrix that will hold the values from the user's input file
		int[][] costMatrix = new int[numStations][numStations];
		// Row index for the cost matrix
		int row = 0;
		// Column index for the cost matrix
		int column = 0;
		while (scan.hasNextLine())
		{
			// The line we just read in from the file
			String line = scan.nextLine();

			Scanner lineScan = new Scanner(line);
			column = row + 1;
			while (lineScan.hasNextInt())
			{
				// The cost for the next cell in the matrix
				int item = lineScan.nextInt();
				costMatrix[row][column] = item;
				column++;
			}

			row++;
			lineScan.close();
		}

		scan.close();
		return costMatrix;
	}

	// Function: optimalCost
	// Inputs: two dimensional cost matrix, number of posts in matrix
	// Returns: Two dimensional array representing the optimal cost traveling
	// from each startpoint to each endpoint
	// optimalCost calculates the minimum cost to travel from any starting post
	// to any end post, and returns the results in an optimal cost matrix
	public static int[][] optimalCost(int[][] costMatrix, int numStations)
	{
		// The optimal cost matrix can be derived from the input costMatrix
		int[][] optimalTripCost = new int[numStations][numStations];

		// initialize the optimal cost matrix
		for (int row = 0; row < numStations; row++)
		{
			for (int col = 0; col < numStations; col++)
			{
				optimalTripCost[row][col] = costMatrix[row][col];
			}
		}

		// Iterate backwards over the matrix
		for (int startStation = numStations - 1; startStation >= 0; startStation--)
		{
			for (int endStation = numStations - 1; endStation >= startStation + 1; endStation--)
			{
				if (endStation - startStation >= 1)
				{
					// The optimal value is the minimum between the current cell
					// under examination, or the previous cell in the row and
					// the cell in the row below
					optimalTripCost[startStation][endStation] = Math.min(
							optimalTripCost[startStation][endStation],
							optimalTripCost[startStation][startStation + 1]
									+ optimalTripCost[startStation + 1][endStation]);
				}
			}
		}

		return optimalTripCost;
	}

	// Function: reportOptimalCostMatrix
	// Inputs: two dimensional optimal cost matrix, original cost matrix, number
	// of posts in each matrix
	// Returns: none
	// reportOptimalCostMatrix will print out the optimal cost matrix that is
	// passed into it, and then use the original cost matrix to identify the
	// rentals that must be made in order to generate the optimal cost from the
	// start to the end post
	public static void reportOptimalCostMatrix(int[][] optimalCost, int[][] costMatrix,
			int numStations)
	{
		// Print the header for the optimal cost matrix
		System.out.println("Optimal Cost Matrix:");
		System.out.print("\t");
		for (int stationIndex = 0; stationIndex < numStations; stationIndex++)
		{
			// Print each station number as a column header
			System.out.print(stationIndex + "\t");
		}

		// Print the optimal cost matrix
		System.out.println();
		for (int startStation = 0; startStation < numStations; startStation++)
		{
			System.out.print(startStation + "\t");
			for (int endStation = 0; endStation < numStations; endStation++)
			{
				// Print a '-' instead of a 0, since all 0 cells should be
				// invalid
				System.out.print(((optimalCost[startStation][endStation] == 0) ? "-"
						: optimalCost[startStation][endStation]) + "\t");
			}

			System.out.println();
		}

		System.out.println();
		// Report the optimal cost to travel from the first to last station
		System.out.println("The lowest cost to get from post 0 to post " + (numStations - 1)
				+ " is: $" + optimalCost[0][numStations - 1]);
		System.out.println("Rentals: ");
		
		// If we hit the end of the row without a change in the matrices, then
		// the row was a rental as well and we used that canoe until the last
		// station
		boolean done = false;
		for (int row = 0; row < numStations - 1; row++)
		{
			for (int col = row + 1; col < numStations; col++)
			{
				// We can identify when we rented a new canoe in our optimal
				// path by comparing cells to the original matrix
				if (optimalCost[row][col] < costMatrix[row][col])
				{
					System.out.println("\tPost " + (row));
					break;
				}
				else if (col == numStations - 1)
				{
					System.out.println("\tPost " + (row));
					done = true;
					break;
				}
			}

			if (done) break;
		}
	}
}
