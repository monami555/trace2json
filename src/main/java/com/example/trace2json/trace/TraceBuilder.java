package com.example.trace2json.trace;

import com.example.trace2json.LogLine;

import java.time.LocalDateTime;


/**
 * Builds a {@link TraceRoot} object that corresponds to a trace tree of all the logs with the same trace ID.
 */
public interface TraceBuilder
{
	/**
	 * Processes given log line, adding up to the trace tree.
	 *
	 * @param logLine the log line
	 */
	void processCall(final LogLine logLine);

	/**
	 * Returns the {@link TraceRoot} that has been built so far.
	 *
	 * @throws TraceIncompleteException in case the trace still contains orphaned nodes
	 * @return the current trace
	 */
	TraceRoot buildTrace() throws TraceIncompleteException;

	/**
	 * Returns the timestamp of when the trace have finished, if already known. If the end time is not yet known,
	 * returns null.
	 *
	 * @return the timestamp of end time or null
	 */
	LocalDateTime getEndTimeOrNull();
}