package com.example.trace2json.process;

import com.example.trace2json.pojo.TraceRoot;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collection;


/**
 * Processes the input log lines of format:
 *
 * [start-timestamp] [end-timestamp] [trace] [service-name] [caller-span]->[span]
 *
 * and outputs log traces as JSON.
 */
public interface LogsProcessor
{
	/**
	 * If a log line belonging to a trace have not shown up after this timestamp time,
	 * the trace is considered to be finished.
	 */
	Duration DEFAULT_TRACE_FINISHED_AFTER = Duration.ofMillis(100);

	void processLog(LocalTime start,
			LocalTime end,
			String traceId,
			String service,
			String callerSpanId,
			String spanId);

	Collection<TraceRoot> popReadyTraces();
}
