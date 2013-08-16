// Programming Assignment 2: Prog2
// Author: John Potocny
// LoginID: poto4766
// Class: CS-203
// Professor: Jim Huggins
package Prog2;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class Prog2
{
	// Flag variables, represent whether calculateConvexHull should calculate
	// the upper hull, lower hull, or both for a given call.
	public final static int HULL_BOTH = 0;
	public final static int HULL_UPPER = -1;
	public final static int HULL_LOWER = 1;

	// Function: main
	// Inputs: String array args
	// Returns: None
	// The main entry point for the application.
	public static void main(String[] args)
	{
		// First determine if the user has passed in valid arguments
		if (!validateArguments(args))
		{
			System.out.println("Usage: Prog2 [Optional: -debug] [filePath]");
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
		catch (FileNotFoundException notFound)
		{
		} // We check this ahead of time, so it shouldn't be possible

		// If pointList returned null, we have nothing to
		// do, and an error message should have already been printed
		if (pointList == null)
		{
			System.out.println("No data available, Exiting Application.");
			System.exit(-1);
		}

		// Get the left and right extreme points for calculating the convex hull
		Point2D.Float leftPoint = pointList.pollFirst();
		Point2D.Float rightPoint = pointList.pollLast();

		long startTime = System.nanoTime();
		// Calculate the convex hull for the list of points in the user's file
		LinkedList<Point2D.Float> hullPoints = calculateConvexHull(pointList, leftPoint,
				rightPoint, HULL_BOTH, debug);
		long endTime = System.nanoTime();

		// Print the lines that make up the convex hull
		reportConvexHull(hullPoints);

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
				pointList = addPointSorted(pointList, inputPoint);
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

	// Function: addPointSorted
	// Inputs: LinkedList of Points pointList, Point inputPoint
	// Returns: LinkedList of Points
	// addPointSorted will add a new Point from the user's input
	// file to the dataSet in sorted order
	public static LinkedList<Point2D.Float> addPointSorted(LinkedList<Point2D.Float> pointList,
			Point2D.Float inputPoint)
	{
		// we need a flag to tell us whether we inserted the item in the middle
		// of the list or not
		boolean added = false;
		for (int listIndex = 0; listIndex < pointList.size(); listIndex++)
		{
			// Skip over the elements of the list, until we
			// find an element that is greater than our item to insert
			if (pointList.get(listIndex).x < inputPoint.x)
			{
				continue;
			}
			else
			{
				// Break when the item is inserted
				pointList.add(listIndex, inputPoint);
				added = true;
				break;
			}
		}

		// Add our input point to the end if it was never added
		if (!added)
		{
			pointList.addLast(inputPoint);
		}

		return pointList;
	}

	// Function: calculateConvexHull
	// Inputs: LinkedList of Points pointList, Point leftPoint,
	// Point rightPoint, boolean debug
	// Returns: LinkedList of Lines
	// calculateConvexHull implements a brute force algorithm to calculate the
	// convex hull for a set of 2D points, and returns the edges that make up
	// the convex hull as a linked list.
	public static LinkedList<Point2D.Float> calculateConvexHull(
			LinkedList<Point2D.Float> pointList, Point2D.Float leftPoint, Point2D.Float rightPoint,
			int hullFlag, boolean debug)
	{
		// This list will contain all of the points for the convex hull
		LinkedList<Point2D.Float> hullPoints = new LinkedList<Point2D.Float>();

		// Return the empty list if the input list is empty
		if (pointList.size() == 0)
		{
			if (leftPoint != null) hullPoints.add(leftPoint);
			if (rightPoint != null) hullPoints.add(rightPoint);

			return hullPoints;
		}

		// These lists will contain points that are above/below the line between
		// the extreme points
		LinkedList<Point2D.Float> upperPoints = new LinkedList<Point2D.Float>();
		LinkedList<Point2D.Float> lowerPoints = new LinkedList<Point2D.Float>();

		// use the line between the left and right extreme points
		// and determine which points are above/below the line
		Line2D.Float extremePointLine = new Line2D.Float(leftPoint, rightPoint);
		int relativePosition = -2;
		for (Point2D.Float inspectionPoint : pointList)
		{
			relativePosition = extremePointLine.relativeCCW(inspectionPoint);
			// a relativeCCW value of -1 means that the point lies above the
			// line
			if (relativePosition == -1)
			{
				upperPoints.addLast(inspectionPoint);
			}
			else
			// treat everything else as below the line
			{
				lowerPoints.addLast(inspectionPoint);
			}
		}

		// The point farthest from the line
		// between the far left and right points.
		Point2D.Float maxPoint = null;
		if (hullFlag == HULL_UPPER || hullFlag == HULL_BOTH)
		{
			// recursive calls for the upper hull
			maxPoint = findFarthestPoint(upperPoints, extremePointLine);

			// Print debug info for upper hull if user specified to do so
			if (debug)
			{
				System.out.println("Upper Hull: left point " + leftPoint + ", right point "
						+ rightPoint + ", extreme point " + maxPoint);
			}

			hullPoints.addAll(calculateConvexHull(upperPoints, leftPoint, maxPoint, -1, debug));
			hullPoints.addAll(calculateConvexHull(upperPoints, maxPoint, rightPoint, -1, debug));
		}

		if (hullFlag == HULL_LOWER || hullFlag == HULL_BOTH)
		{
			// recursive calls for the lower hull
			maxPoint = findFarthestPoint(lowerPoints, extremePointLine);

			// Print debug info for lower hull if user specified to do so
			if (debug)
			{
				System.out.println("Lower Hull: left point " + leftPoint + ", right point "
						+ rightPoint + ", extreme point " + maxPoint);
			}

			hullPoints.addAll(calculateConvexHull(lowerPoints, leftPoint, maxPoint, 1, debug));
			hullPoints.addAll(calculateConvexHull(lowerPoints, maxPoint, rightPoint, 1, debug));
		}
		return hullPoints;
	}

	// Function: findFarthestPoint
	// Inputs: List of Points pointList, Line inspectionLine
	// Returns: Point2D
	// findFarthestPoint compares the input list of points to the referenceLine,
	// and returns the point which is farthest from the line
	public static Point2D.Float findFarthestPoint(LinkedList<Point2D.Float> pointList,
			Line2D.Float inspectionLine)
	{
		double distanceFromLine = -1;
		double farthestDistance = -1;
		Point2D.Float farthestPoint = null;

		for (Point2D.Float inspectionPoint : pointList)
		{
			distanceFromLine = inspectionLine.ptSegDist(inspectionPoint);

			if (distanceFromLine > farthestDistance)
			{
				farthestDistance = distanceFromLine;
				farthestPoint = inspectionPoint;
			}
		}

		pointList.remove(farthestPoint);
		return farthestPoint;
	}

	// Function: reportConvexHull
	// Inputs: LinkedList of Lines hullEdges
	// Returns: None
	// reportConvexHull prints the pairs of points that make up the edges of the
	// convex hull that was computed to the console.
	public static void reportConvexHull(LinkedList<Point2D.Float> hullPoints)
	{
		System.out.println("The Convex Hull is made up of the following lines:");

		// Every two points in the list should be a hull edge
		for (int index = 0; index < hullPoints.size(); index += 2)
		{
			System.out.println("The line between " + hullPoints.get(index) + " and "
					+ hullPoints.get(index + 1));
		}
	}
}
