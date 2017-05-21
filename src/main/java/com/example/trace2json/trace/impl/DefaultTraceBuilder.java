package com.example.trace2json.trace.impl;


import com.example.trace2json.LogLine;
import com.example.trace2json.trace.Trace;
import com.example.trace2json.trace.TraceBuilder;
import com.example.trace2json.trace.TraceIncompleteException;
import com.example.trace2json.trace.TraceRoot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DefaultTraceBuilder implements TraceBuilder
{
	private TraceRoot rootTrace;
	private Map<String, Trace> spanToTrace;
	private LocalDateTime endTime;

	/**
	 * Creates a new instance of the processor for given trace ID.
	 *
	 * @param traceId the trace ID for this trace.
	 */
	public DefaultTraceBuilder(final String traceId)
	{
		this.rootTrace = new TraceRoot();
		this.rootTrace.setId(traceId);
		this.spanToTrace = new HashMap<>();
	}

	@Override
	public void processCall(final LogLine logLine)
	{
		final Trace trace = new Trace();
		trace.setEnd(logLine.getEndTime());
		trace.setService(logLine.getService());
		trace.setStart(logLine.getStartTime());
		trace.setSpan(logLine.getSpanId());
		trace.setCallerSpanId(logLine.getCallerSpanId());
		trace.setOrphaned(true);
		trace.setCalls(new ArrayList<>());

		if (logLine.getCallerSpanId() == null)
		{
			this.endTime = logLine.getEndTime();
			this.rootTrace.setRoot(trace);
			trace.setOrphaned(false);
		}
		spanToTrace.put(trace.getSpan(), trace);
	}

	@Override
	public TraceRoot buildTrace()
	{
		for (Trace trace : spanToTrace.values())
		{
			if (trace.getCallerSpanId() == null)
			{
				continue;
			}
			Trace predecessor = spanToTrace.get(trace.getCallerSpanId());
			if (predecessor != null)
			{
				predecessor.getCalls().add(trace);
				trace.setOrphaned(false);
			}
		}
		if (spanToTrace.values().stream().filter(t -> t.isOrphaned()).findAny().isPresent())
		{
			throw new TraceIncompleteException("The trace '" + rootTrace.getId() + "' is not complete.");
		}
		return rootTrace;
	}

	@Override
	public LocalDateTime getEndTimeOrNull()
	{
		return endTime;
	}

	@Override
	public String toString()
	{
		return rootTrace.getId();
	}
}
