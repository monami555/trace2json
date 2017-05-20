package com.example.trace2json;

import com.example.trace2json.process.CallsProcessor;
import com.example.trace2json.process.impl.DefaultCallsProcessor;

import java.io.Reader;
import java.io.Writer;


public class Trace2JsonProcessor
{
	private Writer outputWriter;
	private Reader inputReader;

	private CallsProcessor logsProcessor = new DefaultCallsProcessor();

	public Trace2JsonProcessor(final Reader inputReader, final Writer outputWriter)
	{
		this.outputWriter = outputWriter;
		this.inputReader = inputReader;
	}
}
