package com.example.trace2json;

import com.example.trace2json.process.LogsProcessor;
import com.example.trace2json.process.impl.DefaultLogsProcessor;

import java.io.Reader;
import java.io.Writer;


public class Trace2JsonProcessor
{
	private Writer outputWriter;
	private Reader inputReader;

	private LogsProcessor logsProcessor = new DefaultLogsProcessor();

	public Trace2JsonProcessor(final Reader inputReader, final Writer outputWriter)
	{
		this.outputWriter = outputWriter;
		this.inputReader = inputReader;
	}
}
