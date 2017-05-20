package com.example.trace2json.process.impl;


import com.example.trace2json.pojo.Trace;
import com.example.trace2json.pojo.TraceRoot;
import com.example.trace2json.process.TraceBuilder;

import java.util.Collection;


public class DefaultTraceBuilder implements TraceBuilder
{
	private TraceRoot rootTrace;
	private Collection<Trace> traces;

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
	public void processLog(final String serviceName, final String spanFrom, final String spanTo)
	{

	}

	@Override
	public TraceRoot buildTrace()
	{
		return null;
	}
}
