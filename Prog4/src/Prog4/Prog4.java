package Prog4;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Prog4
{
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

		File inputFile = new File(fileName);
		// Check if the user's input file exists before using it
		if (!inputFile.exists())
		{
			System.out.printf("User input file %s not found.", inputFile);
			System.exit(-1);
		}

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

		int[][] optimalCost = optimalCost(costMatrix, costMatrix.length);
		reportOptimalCostMatrix(optimalCost, costMatrix, optimalCost.length);
	}

	public static int[][] parseInputFile(File inputFile) throws FileNotFoundException
	{
		Scanner scan = new Scanner(inputFile);
		int numStations = scan.hasNextInt() ? scan.nextInt() : -1;
		scan.nextLine();
		if (numStations == -1)
		{
			scan.close();
			return null;
		}

		int[][] costMatrix = new int[numStations][numStations];
		int row = 0;
		int column = 0;
		while (scan.hasNextLine())
		{
			String line = scan.nextLine();
			Scanner lineScan = new Scanner(line);
			column = row + 1;
			while (lineScan.hasNextInt())
			{
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

	public static int[][] optimalCost(int[][] costMatrix, int numStations)
	{
		int[][] optimalTripCost = costMatrix;
		for (int startStation = numStations-1; startStation >= 0; startStation--)
		{
			for (int endStation = numStations-1; endStation >= startStation + 1; endStation--)
			{
				if (endStation - startStation > 1)
				{
					optimalTripCost[startStation][endStation] = Math.min(
							optimalTripCost[startStation][endStation],
							optimalTripCost[startStation][startStation + 1]
									+ optimalTripCost[startStation + 1][endStation]);
				}
			}
		}

		return optimalTripCost;
	}

	public static void reportOptimalCostMatrix(int[][] optimalCost, int[][] costMatrix, int numStations)
	{
		System.out.println("Optimal Cost Matrix:");
		System.out.print("\t");
		for (int stationIndex = 0; stationIndex < numStations; stationIndex++)
		{
			// Print each station number as a column header
			System.out.print(stationIndex + "\t");
		}

		// Print the cost matrix
		System.out.println();
		for (int startStation = 0; startStation < numStations; startStation++)
		{
			System.out.print(startStation + "\t");
			for (int endStation = 0; endStation < numStations; endStation++)
			{
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
		System.out.println("\tPost 0");
		for (int row = 0; row < numStations; row++)
		{
			for (int col = 0; col < numStations; col++)
			{
				if (col - row > 1)
				{
					if(costMatrix[row][col] >= (costMatrix[row][row + 1] + costMatrix[row + 1][col]))
					{
						System.out.println("\tPost " + (col-1));
						break;
					}
				}
			}
		}
	}
}
