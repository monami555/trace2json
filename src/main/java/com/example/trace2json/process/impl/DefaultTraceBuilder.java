package com.example.trace2json.process.impl;


import com.example.trace2json.pojo.Trace;
import com.example.trace2json.pojo.TraceRoot;
import com.example.trace2json.Call;
import com.example.trace2json.process.TraceBuilder;

import java.time.LocalTime;
import java.util.Collection;


public class DefaultTraceBuilder implements TraceBuilder
{
	private TraceRoot rootTrace;
	private Collection<Trace> traces;
	private LocalTime endTime;

	/**
	 * Creates a new instance of the processor for given trace ID.
	 *
	 * @param traceId the trace ID for this trace.
	 */
	public DefaultTraceBuilder(final String traceId)
	{
		this.rootTrace = new TraceRoot();
		this.rootTrace.setTrace(traceId);
	}

	@Override
	public void processCall(final Call call)
	{
		if (call.getCallerSpanId() == null)
		{
			this.endTime = call.getEndTime();
		}
	}

	@Override
	public TraceRoot buildTrace()
	{
		return null;
	}

	@Override
	public LocalTime getEndTimeOrNull()
	{
		return endTime;
	}
}
