package com.example.trace2json;


import com.example.trace2json.impl.DefaultLogsProcessor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class Trace2JsonCli
{
	private final static LogsProcessor logsProcessor = new DefaultLogsProcessor();

	public static void main(final String... args)
	{
		if (args.length != 2)
		{
			printHelp();
			return;
		}

		// small 444 ms
		// medium 6118 ms

		// after hashmap
		// small 284 ms, 306ms for building
		// medium 4311 ms, 3322 ms for building

		try
		{
			logsProcessor.process(
					"STDIN".equals(args[0]) ? new InputStreamReader(System.in) : new FileReader(new File(args[0])),
					"STDOUT".equals(args[1]) ? new PrintWriter(System.out) : new FileWriter(new File(args[1])));
		}
		catch (IOException e)
		{
			System.err.println("Problem opening file: " + e.getMessage());
		}
	}

	private static void printHelp()
	{
		System.out.println("This program reads log lines of service calls and converts them to JSON trees. ");
		System.out.println();
		System.out.println("Usage: ");
		System.out.println("  trace2json inputFile outputFile");
		System.out.println();
		System.out.println("Use 'STDIN' or 'STDOUT' to read from or print to stdout, e.g.:");
		System.out.println("  trace2json STDIN STDOUT");
	}

}
