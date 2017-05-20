package com.example.trace2json.process;

import com.example.trace2json.pojo.TraceRoot;


/**
 * Builds a log trace.
 */
public interface TraceBuilder
{
	/**
	 * Processes given log information.
	 *
	 * @param service service that has logged the line
	 * @param spanFrom span ID of the caller
	 * @param spanTo span ID of the receiver
	 */
	void processLog(final String service, final String spanFrom, final String spanTo);

	/**
	 * Returns the trace that has been built so far.
	 *
	 * @return the current trace
	 */
	TraceRoot buildTrace();
}
