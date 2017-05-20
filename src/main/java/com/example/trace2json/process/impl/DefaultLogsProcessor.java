package com.example.trace2json.process.impl;

import com.example.trace2json.pojo.TraceRoot;
import com.example.trace2json.process.LogsProcessor;
import com.example.trace2json.process.TraceBuilder;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class DefaultLogsProcessor implements LogsProcessor
{
	private static final Duration traceFinishedAfter = DEFAULT_TRACE_FINISHED_AFTER;
	private static ConcurrentMap<String, TraceBuilder> traceBuilders = new ConcurrentHashMap<>();

	@Override
	public void processLog(final LocalTime start,
			final LocalTime end,
			final String traceId,
			final String service,
			final String callerSpanId,
			final String spanId)
	{
		final TraceBuilder traceBuilder = traceBuilders.get(traceId);
		if (traceBuilder != null)
		{
			traceBuilder.processLog(service, callerSpanId, spanId);
		}
		else
		{
			final TraceBuilder newTraceBuilder = new DefaultTraceBuilder(traceId);
			synchronized (traceBuilders)
			{
				if (traceBuilders.get(traceId) == null)
				{
					traceBuilders.put(traceId, newTraceBuilder);
				}
				else
				{
					throw new IllegalArgumentException("Multiple root traces with same id");
				}
			}
		}
	}

	@Override
	public Collection<TraceRoot> popReadyTraces()
	{
		return null;
	}
}
