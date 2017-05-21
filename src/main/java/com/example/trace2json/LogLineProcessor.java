package com.example.trace2json;

import com.example.trace2json.trace.TraceRoot;

import java.time.Duration;
import java.util.Collection;



public interface LogLineProcessor
{
	/**
	 * If a log line belonging to a trace have not shown up after this timestamp time,
	 * the trace is considered to be finished.
	 */
	Duration DEFAULT_TRACE_FINISHED_AFTER = Duration.ofMillis(1000);

	/**
	 * Maximum number of traces kept in memory in parallel before the finished ones are removed.
	 */
	int DEFAULT_TRACE_NUMBER_BUFFER = 10000;

	/**
	 * Process the log line.
	 *
	 * @param logLine the log line
	 */
	void processLogLine(LogLine logLine);

	/**
	 * Remove and return the trace trees that have enough information and can be safely assumed to be complete,
	 * according to {@link #DEFAULT_TRACE_FINISHED_AFTER}.
	 *
	 * @param force remove and return all the trace trees, even if they are not sure to be complete
	 * @return the requested trace trees
	 */
	Collection<TraceRoot> popReadyTraces(final boolean force);
}
