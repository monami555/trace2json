package com.example.trace2json;

import java.time.LocalTime;


/**
 * Immutable.
 */
public class Call
{
	private LocalTime startTime;
	private LocalTime endTime;
	private String traceId;
	private String service;
	private String callerSpanId;
	private String spanId;

	public Call(final LocalTime startTime,
			final LocalTime endTime,
			final String traceId,
			final String service,
			final String callerSpanId,
			final String spanId)
	{
		this.startTime = startTime;
		this.endTime = endTime;
		this.traceId = traceId;
		this.service = service;
		this.callerSpanId = callerSpanId;
		this.spanId = spanId;

	}

	public LocalTime getStartTime()
	{
		return startTime;
	}

	public LocalTime getEndTime()
	{
		return endTime;
	}

	public String getTraceId()
	{
		return traceId;
	}

	public String getService()
	{
		return service;
	}

	public String getCallerSpanId()
	{
		return callerSpanId;
	}

	public String getSpanId()
	{
		return spanId;
	}
}
