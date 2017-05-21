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


/**
 * Processes a single line of input.
 *
 * For each line the {@link #lastEndTime} is recorded, in order to later judge which trace trees may indeed be finished
 * in the {@link #popReadyTraces} method (there is still a slight chance of an extra log line about same trace coming
 * after the end of the first call with this trace id), assuming a delay of {@link #epsilon}.
 *
 * For each line a {@link TraceBuilder} is looked up by the trace ID, or created if it doesn't exist. The trace
 * builder takes care of further processing of the log.
 *
 */
public class DefaultLogLineProcessor implements LogLineProcessor
{
	private Duration epsilon = DEFAULT_TRACE_FINISHED_AFTER;
	private int traceNumberBuffer = DEFAULT_TRACE_NUMBER_BUFFER;

	private Map<String, TraceBuilder> traceBuilders = new ConcurrentHashMap<>();
	private LocalDateTime lastEndTime;

	@Override
	public void processLogLine(LogLine logLine)
	{
		updateEndTimeIfLater(logLine);

		final String traceId = logLine.getTraceId();
		TraceBuilder traceBuilder = traceBuilders.get(traceId);
		if (traceBuilder == null)
		{
			traceBuilder = new DefaultTraceBuilder(traceId);
			traceBuilders.put(traceId, traceBuilder);
		}
		traceBuilder.processCall(logLine);
	}

	private void updateEndTimeIfLater(final LogLine logLine)
	{
		if (lastEndTime == null || (logLine.getEndTime() != null && logLine.getEndTime().isAfter(lastEndTime)))
		{
			lastEndTime = logLine.getEndTime();
		}
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
							result.add(traceBuilders.remove(traceId).buildTrace());
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
