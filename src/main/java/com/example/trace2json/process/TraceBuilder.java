package com.example.trace2json.process;

import com.example.trace2json.Call;
import com.example.trace2json.pojo.TraceRoot;

import java.time.LocalTime;


/**
 * Builds a log trace.
 */
public interface TraceBuilder
{
	/**
	 * Processes given log information.
	 *
	 * @param call
	 */
	void processCall(final Call call);

	/**
	 * Returns the trace that has been built so far.
	 *
	 * @return the current trace
	 */
	TraceRoot buildTrace();

	/**
	 * Returns the timestamp of when the trace have finished, if already known. If the end time is not yet known,
	 * returns null.
	 *
	 * @return the timestamp of end time or null
	 */
	LocalTime getEndTimeOrNull();
}
