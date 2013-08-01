// Program: Prog1
// Author: John Potocny
// Class: CS-203
// Professor: Jim Huggins
package Prog1;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.io.*;

public class Prog1
{
	// Function: main
	// Inputs: String array args
	// Returns: None
	// The main entry point for the application.
	public static void main(String[] args)
	{
		// First determine if the user has passed in valid arguments
		if (!validateArguments(args))
		{
			System.out.println("Usage: Prog1 [Optional: -debug] [filePath]");
			System.exit(-2);
		}

		String fileName = (args.length == 2) ? args[1] : args[0];
		File inputFile = new File(fileName);

		// Check if the user's input file exists before using it
		if (!inputFile.exists())
		{
			System.out.printf("User input file %s not found.", args[1]);
			System.exit(-1);
		}

		LinkedList<Point2D.Float> pointList = null;
		// If the file exists, parse the contents into a data structure
		try
		{
			pointList = parseInputFile(inputFile);
		}
		catch (FileNotFoundException e)
		{
		} // We check this ahead of time, so it shouldn't be possible

		// If pointList returned null, we have nothing to
		// do, and an error message should have already
		// been printed
		if (pointList == null)
		{
			System.out.println("No data available, Exiting Application.");
			System.exit(-1);
		}

		// Use the list of points that we created from the user's
		// input file to calculate the convex hull for the set of points.

		// Add call to convex hull function here
		calculateConvexHull(pointList);
	}

	// Function: validateArguments
	// Input: String[] args
	// Returns: Boolean
	// validateArguments looks at the arguments passed in to the application by
	// the user, and determines whether they are a valid configuration.
	public static boolean validateArguments(String[] args)
	{
		if (args.length == 0)
		{
			return false;
		}
		else if (args.length == 2)
		{
			// If two arguments have been passed in,
			// the first arg must be the text "-debug"
			if (!args[0].equalsIgnoreCase("-debug"))
			{
				return false;
			}
		}

		return true;
	}

	// Function: parseInputFile
	// Inputs: File inputFile
	// Returns: LinkedList of 2D Points [Float]
	// parseInputFile will read the user's inputFile line by line, and
	// parse out the two floating point numbers that should be on each line,
	// separated by a space. These numbers are made into a point in 2D space,
	// and added to the list of points that will be returned.
	public static LinkedList<Point2D.Float> parseInputFile(File inputFile)
			throws FileNotFoundException
	{
		// Scanner object to read the contents of the user's file
		Scanner reader = new java.util.Scanner(inputFile);
		// Temp string to store the latest line read from the user file
		String fileLine = "";
		// Array for the two points to be stored into, once the string
		// containing them is split
		String[] rawPoints;
		// Temp point object used to parse the individual points in the file
		Point2D.Float inputPoint;
		// Our list for the points that we parse out of the user's file
		LinkedList<Point2D.Float> pointList = new LinkedList<Point2D.Float>();
		try
		{
			while (reader.hasNextLine()) // Read to the end of the file
			{
				// Get the next line of the user's file
				fileLine = reader.nextLine();
				// Parse out the points as an array with two string elements
				rawPoints = fileLine.split(" ", 2);

				// Parse our string elements into floats and create a point
				inputPoint = new Point2D.Float(Float.parseFloat(rawPoints[0]),
						Float.parseFloat(rawPoints[1]));

				// Add the new point to our list
				pointList.add(inputPoint);
			}
		}
		// Getting here means we had a bad line
		// in the file
		catch (NumberFormatException ex)
		{
			// Alert the user to why we failed
			System.out.printf("Invalid input in user file:",
					"%s does not contain two floating point numbers", fileLine);
			return null; // we will return null for all bad inputs
		}
		finally
		{
			reader.close();
		}

		return pointList;
	}

	public static void calculateConvexHull(LinkedList<Point2D.Float> pointList)
	{
		LinkedList<Line2D.Float> hullEdges = new LinkedList<Line2D.Float>();
		int relativePositionIndicator = 2;
		for (int startPointIndex = 0; startPointIndex < pointList.size(); startPointIndex++)
		{
			for (int endPointIndex = startPointIndex + 1; endPointIndex < pointList
					.size(); endPointIndex++)
			{
				Line2D.Float inspectionLine = new Line2D.Float(
						pointList.get(startPointIndex),
						pointList.get(endPointIndex));
				boolean isHullEdge = true;
				relativePositionIndicator = 2;
				for (int inspectionPointIndex = 0; inspectionPointIndex < pointList
						.size(); inspectionPointIndex++)
				{
					if (inspectionPointIndex == endPointIndex
							|| inspectionPointIndex == startPointIndex)
					{
						continue;
					}

					if (relativePositionIndicator == 2)
					{
						relativePositionIndicator = inspectionLine
								.relativeCCW(pointList
										.get(inspectionPointIndex));
					}
					else
					{
						if (inspectionLine.relativeCCW(pointList
								.get(inspectionPointIndex)) != relativePositionIndicator)
						{
							isHullEdge = false;
							break;
						}
					}
				}

				if (isHullEdge)
				{
					hullEdges.add(inspectionLine);
					System.out.println("Add the line to convex hull:");
					System.out.println("Start Point: " + inspectionLine.getP1());
					System.out.println("End Point: " + inspectionLine.getP2());
				}
			}
		}
	}
}
