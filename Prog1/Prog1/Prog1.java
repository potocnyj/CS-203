// Programming Assignment 1: Prog1
// Author: John Potocny
// LoginID: poto4766
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
		boolean debug = (args.length == 2) ? true : false;
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

		long startTime = System.nanoTime();
		// Add call to convex hull function here
		LinkedList<Line2D.Float> hullEdges = calculateConvexHull(pointList, debug);
		long endTime = System.nanoTime();

		reportConvexHull(hullEdges);

		System.out.println("Calculation of Convex Hull took " + (endTime - startTime)
				+ " nanoseconds.");
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
			// Always close the handle on the file
			reader.close();
		}

		return pointList;
	}

	// Function: calculateConvexHull
	// Inputs: LinkedList of Points pointList, boolean debug
	// Returns: LinkedList of Lines
	// calculateConvexHull implements a brute force algorithm to calculate the
	// convex hull for a set of 2D points, and returns the edges that make up
	// the convex hull as a linked list.
	public static LinkedList<Line2D.Float> calculateConvexHull(
			LinkedList<Point2D.Float> pointList, boolean debug)
	{
		LinkedList<Line2D.Float> hullEdges = new LinkedList<Line2D.Float>();
		int relativePositionIndicator = 2;
		for (int startIndex = 0; startIndex < pointList.size(); startIndex++)
		{
			for (int endIndex = startIndex + 1; endIndex < pointList.size(); endIndex++)
			{
				// Try and make a hull edge using every pair of points in the
				// list
				Line2D.Float inspectionLine = new Line2D.Float(pointList.get(startIndex),
						pointList.get(endIndex));

				if (debug)
				{
					System.out.println("Points under consideration: "
							+ pointList.get(startIndex) + " " + pointList.get(endIndex));
				}

				boolean isHullEdge = true; // Assume isHullEdge by default
				// Initialize to 2 because relativeCCW can't return it
				relativePositionIndicator = 2;
				int ptsLeftSide = 0;
				int ptsRightSide = 0;

				// Loop over every point in the linkedList, and determine
				// whether or not all of the points relative to the inspection
				// line are on the same side of the line.
				for (Point2D.Float inspectionPoint : pointList)
				{
					// This if statement solves a deficiency in the
					// Line2D.relativeCCW function. This will identify points
					// that are colinear to the inspection line, even if they
					// are outside of the range of the defining points for the
					// line (which relativeCCW will not catch).
					// If a point satisfies this condition, then there is a line
					// that is better suited for use in the convex hull than the
					// one we are currently considering.
					if (inspectionLine.ptSegDist(inspectionPoint) > 0
							&& inspectionLine.ptLineDist(inspectionPoint) == 0)
					{
						isHullEdge = false;
					}

					int pointRelativePosition = inspectionLine
							.relativeCCW(inspectionPoint);
					if (pointRelativePosition == 1)
					{
						ptsRightSide++;
					}
					else if (pointRelativePosition == -1)
					{
						ptsLeftSide++;
					}

					if (pointRelativePosition == 0)
					{
						continue;
					}
					// If we haven't initialized it, we won't do a comparison
					else if (relativePositionIndicator == 2)
					{
						relativePositionIndicator = pointRelativePosition;
					}
					else if (pointRelativePosition != relativePositionIndicator)
					{
						isHullEdge = false;
					}
				}

				if (debug)
				{
					System.out.println("Found " + ptsRightSide
							+ " points to the right of the line, and " + ptsLeftSide
							+ " points to the left of the line.");
				}

				// If the line only has points on one side of it, then it is a
				// hull edge
				if ((ptsRightSide > 0 && ptsLeftSide > 0) || isHullEdge == false)
				{
					if (debug)
					{
						System.out
								.println("Line is not a part of the convex hull, ignoring.");
					}
				}
				else
				{
					hullEdges.add(inspectionLine);
					if (debug)
					{
						System.out
								.println("Adding the line as a part of the convex hull.");
					}
				}
			}
		}

		return hullEdges;
	}

	// Function: reportConvexHull
	// Inputs: LinkedList of Lines hullEdges
	// Returns: None
	// reportConvexHull prints the pairs of points that make up the edges of the
	// convex hull that was computed to the console.
	public static void reportConvexHull(LinkedList<Line2D.Float> hullEdges)
	{
		System.out.println("The Convex Hull is made up of the following lines:");

		for (Line2D.Float edge : hullEdges)
		{
			System.out.println("The line between " + edge.getP1() + " and "
					+ edge.getP2());
		}
	}
}
