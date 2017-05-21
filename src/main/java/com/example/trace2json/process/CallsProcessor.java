package com.example.trace2json.process;

import com.example.trace2json.Call;
import com.example.trace2json.pojo.TraceRoot;

import java.time.Duration;
import java.util.Collection;


/**
 * Processes the input log lines of format:
 *
 * [start-timestamp] [end-timestamp] [trace] [service-name] [caller-span]->[span]
 *
 * and outputs log traces as JSON.
 */
public interface CallsProcessor
{
	/**
	 * If a log line belonging to a trace have not shown up after this timestamp time,
	 * the trace is considered to be finished.
	 */
	Duration DEFAULT_TRACE_FINISHED_AFTER = Duration.ofMillis(10000);

	void processCall(Call call);

	Collection<TraceRoot> popReadyTraces(final boolean force);
}
