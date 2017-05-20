package com.example.trace2json.process.impl;

import com.example.trace2json.Call;
import com.example.trace2json.pojo.TraceRoot;
import com.example.trace2json.process.CallsProcessor;
import com.example.trace2json.process.TraceBuilder;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DefaultCallsProcessor implements CallsProcessor
{
	private Duration epsilon = DEFAULT_TRACE_FINISHED_AFTER;

	private Map<String, TraceBuilder> traceBuilders = new ConcurrentHashMap<>();
	private LocalTime lastEndTime;

	@Override
	public void processCall(Call call)
	{
		if (lastEndTime == null || call.getEndTime().isAfter(lastEndTime))
		{
			lastEndTime = call.getEndTime();
		}
		TraceBuilder traceBuilder = traceBuilders.get(call.getTraceId());
		if (traceBuilder == null)
		{
			traceBuilder = new DefaultTraceBuilder(call.getTraceId());
			traceBuilders.put(call.getTraceId(), new DefaultTraceBuilder(call.getTraceId()));
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

	public LocalTime getLastEndTime()
	{
		return lastEndTime;
	}

	public void setEpsilon(final Duration epsilon)
	{
		this.epsilon = epsilon;
	}

	private boolean isTraceOld(LocalTime endTime)
	{
		return endTime != null && endTime.plus(epsilon).isBefore(lastEndTime);
	}
}
