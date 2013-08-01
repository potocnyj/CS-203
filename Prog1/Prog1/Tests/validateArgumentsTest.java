package Prog1.Tests;

import static org.junit.Assert.*;
import org.junit.Test;
import Prog1.Prog1;

public class validateArgumentsTest
{
	@Test
	public void test()
	{
		// Valid arg string - with debug flag
		String[] args = {"-debug", "C:/Test.txt"};
		assertTrue(Prog1.validateArguments(args));
		
		// Valid arg string - no debug flag
		args = new String[] {"C:/Test.txt"};
		assertTrue(Prog1.validateArguments(args));		
		
		// Invalid arg string - debug flag in wrong order
		args = new String[] {"C:/Test.txt", "-debug"};
		assertTrue(!Prog1.validateArguments(args));
		
		// Invalid arg string - no args
		args = new String[] {};
		assertTrue(!Prog1.validateArguments(args));
	}
}
