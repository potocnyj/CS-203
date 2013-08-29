// Programming Assignment 3: Prog3
// Author: John Potocny
// LoginID: poto4766
// Class: CS-203
// Professor: Jim Huggins

import java.util.*;
import java.util.Map.Entry;
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
		// Get the fileName that the user specified on the command line
		String fileName = (args.length == 1) ? args[0] : null;
		File inputFile = null;
		if (fileName != null)
		{
			inputFile = new File(fileName);
		}
		else
		{
			// If no file was specified, exit
			System.out.println("Usage: Prog3 [filePath]");
			System.exit(-2);
		}

		// Check if the user's input file exists before using it
		if (!inputFile.exists())
		{
			System.out.printf("User input file %s not found.", inputFile);
			System.exit(-1);
		}

		HashMap<String, LinkedList<String>> anagramMap = null;
		// Read the data from the user's input file
		try
		{
			anagramMap = parseInputFile(inputFile);
		}
		catch (FileNotFoundException notFound)
		{
			notFound.printStackTrace();
		}

		if (anagramMap == null)
		{
			System.out.println("Parse input failed. Check to make "
					+ "sure file is not empty and is correctly formatted.");
			System.exit(-1);
		}

		reportAnagramGroups(anagramMap);
	}

	// Function: reportAnagramGroups
	// Inputs: HashMap, String keys, LinkedList<String> values
	// Returns: none
	// reportAnagramGroups iterates over the lists of anagram groups for every
	// sorted anagram key in the HashMap, and prints out the Anagram groups to
	// the console.
	public static void reportAnagramGroups(HashMap<String, LinkedList<String>> anagramMap)
	{
		// The LinkedList for every entry in our HashMap
		LinkedList<String> anagramList = null;
		for (Entry<String, LinkedList<String>> mapEntry : anagramMap.entrySet())
		{
			anagramList = mapEntry.getValue();
			// We need at least 2 items in the list for a valid anagram match
			if (anagramList.size() < 2)
			{
				continue;
			}

			// Print the words that are anagrams of each other
			System.out.println("The following words are anagrams of each other:");
			for (String word : anagramList)
			{
				System.out.println("\t" + word);
			}
		}
	}

	// Function: parseInputFile
	// Inputs: File inputFile
	// Returns: HashMap, mapping Lists of words to anagram keys
	// parseInputFile will read the user's inputFile line by line, and
	// parse out the words that should be on each line. These words
	// are character sorted into anagram keys, and added to a hash map
	// based on the sorted character anagram key
	public static HashMap<String, LinkedList<String>> parseInputFile(File inputFile)
			throws FileNotFoundException
	{
		// Scanner object to read the contents of the user's file
		Scanner reader = new java.util.Scanner(inputFile);
		// Temp string to store the latest line read from the user file
		String fileLine = "";
		// Sorted characters for the word that was read from the file
		char[] anagramChars = null;
		String anagram = null;
		// Our master map, will contain sets of anagrams mapped by the anagram
		// key
		HashMap<String, LinkedList<String>> anagramMap = new HashMap<>();

		// A linkedlist that we will use to build groups of anagrams and add
		// them to our Hash Map
		LinkedList<String> anagramList = null;
		while (reader.hasNextLine())
		{
			fileLine = reader.nextLine().trim();
			// Anagrams will be case invariant, make all chars lower
			anagramChars = fileLine.toLowerCase().toCharArray();
			Arrays.sort(anagramChars);
			anagram = String.valueOf(anagramChars);

			// Add the line to the list corresponding to its anagram key
			anagramList = anagramMap.get(anagram);
			if (anagramList == null)
			{
				anagramList = new LinkedList<String>();
			}

			anagramList.addLast(fileLine);
			// We should only have to modify the hash map if the anagram entry
			// didn't already exist
			if (!anagramMap.containsKey(anagram))
			{
				anagramMap.put(anagram, anagramList);
			}
		}

		reader.close();
		return anagramMap;
	}
}
