// Programming Assignment 3: Prog3
// Author: John Potocny
// LoginID: poto4766
// Class: CS-203
// Professor: Jim Huggins

import java.util.*;
import java.awt.geom.Point2D;
import java.io.*;

public class Prog3
{
	// Function: main
	// Inputs: String array args
	// Returns: None
	// The main entry point for the application.
	public static void main(String[] args)
	{
		String fileName = (args.length == 1) ? args[0] : null;
		File inputFile = null;
		if(fileName != null)
		{
			inputFile = new File(fileName);
		}
		
		// Check if the user's input file exists before using it
		if (!inputFile.exists())
		{
			System.out.printf("User input file %s not found.", inputFile);
			System.exit(-1);
		}		
	}
	
	
	public static void parseInputFile(File inputFile) throws FileNotFoundException
	{
		// Scanner object to read the contents of the user's file
			Scanner reader = new java.util.Scanner(inputFile);
			// Temp string to store the latest line read from the user file
			String fileLine = "";			
	}
}
