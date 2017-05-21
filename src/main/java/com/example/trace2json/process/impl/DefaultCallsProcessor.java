package com.example.trace2json.process.impl;

import com.example.trace2json.Call;
import com.example.trace2json.pojo.TraceRoot;
import com.example.trace2json.process.CallsProcessor;
import com.example.trace2json.process.TraceBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DefaultCallsProcessor implements CallsProcessor
{
	private Duration epsilon = DEFAULT_TRACE_FINISHED_AFTER;

	private Map<String, TraceBuilder> traceBuilders = new ConcurrentHashMap<>();
	private LocalDateTime lastEndTime;

	@Override
	public void processCall(Call call)
	{
		if (lastEndTime == null || call.getEndTime().isAfter(lastEndTime))
		{
			lastEndTime = call.getEndTime();
		}
		final String traceId = call.getTraceId();
		TraceBuilder traceBuilder = traceBuilders.get(traceId);
		if (traceBuilder == null)
		{
			traceBuilder = new DefaultTraceBuilder(traceId);
			traceBuilders.put(traceId, traceBuilder);
		}
		traceBuilder.processCall(call);
	}

	@Override
	public Collection<TraceRoot> popReadyTraces()
	{
		// TODO make parallelizable!
		List<TraceRoot> result = new ArrayList<>();
		traceBuilders
				.forEach((traceId, builder) -> {
					if (isTraceOld(builder.getEndTimeOrNull()))
					{
						result.add(traceBuilders.remove(traceId).buildTrace());
					}
				});
		return result;
	}

	public LocalDateTime getLastEndTime()
	{
		return lastEndTime;
	}

	public void setEpsilon(final Duration epsilon)
	{
		this.epsilon = epsilon;
	}

	private boolean isTraceOld(LocalDateTime endTime)
	{
		return endTime != null && endTime.plus(epsilon).isBefore(lastEndTime);
	}
}
