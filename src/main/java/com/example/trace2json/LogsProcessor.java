package com.example.trace2json;

import java.io.Reader;
import java.io.Writer;


/**
 * Processes the input log lines of format:
 *
 * [start-timestamp] [end-timestamp] [trace] [service-name] [caller-span]->[span]
 *
 * coming from a {@link java.io.Reader} and outputs log trace trees as JSON using the provided {@link java.io.Writer}.
 */
public interface LogsProcessor
{
	String[] ALLOWED_INPUT_DATE_TIME_FORMATS = new String[]{
			"yyyy-MM-dd'T'HH:mm:ss.SS'Z'",
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
			"yyyy-MM-dd'T'HH:mm:ss'Z'"};

	/**
	 * Runs the processing for input coming from supplied {@link Reader} and writing the output using supplied
	 * {@link Writer}.
	 *
	 * @param inputReader the input of the data
	 * @param outputWriter where the output should be written to
	 */
	void process(final Reader inputReader, final Writer outputWriter);
}
