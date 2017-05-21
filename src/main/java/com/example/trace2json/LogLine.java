package com.example.trace2json;

import java.time.LocalDateTime;


/**
 * Immutable POJO describing a single log line.
 */
public class LogLine
{
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String traceId;
	private String service;
	private String callerSpanId;
	private String spanId;

	public LogLine(final LocalDateTime startTime,
			final LocalDateTime endTime,
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

	public LocalDateTime getStartTime()
	{
		return startTime;
	}

	public LocalDateTime getEndTime()
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
