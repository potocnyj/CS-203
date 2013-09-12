package Prog4;

import java.util.*;
import java.io.*;

public class Prog4
{
	public static void main(String[] args) throws FileNotFoundException
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

	public static void calculateOptimalCost(int[][] costMatrix, int numStations)
	{
		int[][] optimalTripCost = costMatrix;
		
		for (int row = 0; row < numStations; row++)
		{
			for(int column = 1; column < numStations; column++)
			{
				
			}
		}
	}
}