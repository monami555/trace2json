package com.example.trace2json.impl;

import com.example.trace2json.LogLine;
import com.example.trace2json.LogLineProcessor;
import com.example.trace2json.trace.TraceBuilder;
import com.example.trace2json.trace.TraceRoot;
import com.example.trace2json.trace.impl.DefaultTraceBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DefaultLogLineProcessor implements LogLineProcessor
{
	private static Duration epsilon = DEFAULT_TRACE_FINISHED_AFTER;
	private static int traceNumberBuffer = DEFAULT_TRACE_NUMBER_BUFFER;

	private static Map<String, TraceBuilder> traceBuilders = new ConcurrentHashMap<>();
	private static LocalDateTime lastEndTime;

	@Override
	public void processLogLine(LogLine logLine)
	{
		if (lastEndTime == null || logLine.getEndTime().isAfter(lastEndTime))
		{
			lastEndTime = logLine.getEndTime();
		}
		final String traceId = logLine.getTraceId();
		TraceBuilder traceBuilder = traceBuilders.get(traceId);
		if (traceBuilder == null)
		{
			traceBuilder = new DefaultTraceBuilder(traceId);
			traceBuilders.put(traceId, traceBuilder);
		}
		traceBuilder.processCall(logLine);
	}

	@Override
	public Collection<TraceRoot> popReadyTraces(final boolean force)
	{
		if (force || traceBuilders.size() > traceNumberBuffer)
		{
			final List<TraceRoot> result = new ArrayList<>();
			traceBuilders
					.forEach((traceId, builder) -> {
						if (force || isTraceOld(builder.getEndTimeOrNull()))
						{
							result.add(traceBuilders.remove(traceId)
									.buildTrace());
						}
					});
			return result;
		}
		else
		{
			return Collections.emptyList();
		}
	}

	public LocalDateTime getLastEndTime()
	{
		return lastEndTime;
	}

	public void setEpsilon(final Duration epsilon)
	{
		this.epsilon = epsilon;
	}

	public void setTraceNumberBuffer(final int traceNumberBuffer)
	{
		this.traceNumberBuffer = traceNumberBuffer;
	}

	private boolean isTraceOld(LocalDateTime endTime)
	{
		return endTime != null && endTime.plus(epsilon).isBefore(lastEndTime);
	}
}
