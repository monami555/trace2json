package com.example.trace2json.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.Collection;


public class Trace
{
	@JsonIgnore
	private String callerSpanId;

	@JsonIgnore
	private boolean orphaned;

	private String span;

	private String service;

	@JsonSerialize(using = DateTimeSerialiser.class)
	private LocalDateTime start;

	@JsonSerialize(using = DateTimeSerialiser.class)
	private LocalDateTime end;

	private Collection<Trace> calls;

	public boolean isOrphaned()
	{
		return orphaned;
	}

	public String getCallerSpanId()
	{
		return callerSpanId;
	}

	public String getSpan()
	{
		return span;
	}

	public String getService()
	{
		return service;
	}

	public LocalDateTime getStart()
	{
		return start;
	}

	public LocalDateTime getEnd()
	{
		return end;
	}

	public Collection<Trace> getCalls()
	{
		return calls;
	}

	public void setOrphaned(final boolean isOrphaned)
	{
		this.orphaned = isOrphaned;
	}

	public void setSpan(final String span)
	{
		this.span = span;
	}

	public void setCallerSpanId(final String callerSpanId)
	{
		this.callerSpanId = callerSpanId;
	}

	public void setService(final String service)
	{
		this.service = service;
	}

	public void setStart(final LocalDateTime start)
	{
		this.start = start;
	}

	public void setEnd(final LocalDateTime end)
	{
		this.end = end;
	}

	public void setCalls(final Collection<Trace> calls)
	{
		this.calls = calls;
	}

	@Override
	public String toString()
	{
		return "Trace{" +
				"callerSpanId='" + callerSpanId + '\'' +
				", span='" + span + '\'' +
				", orphaned=" + orphaned +
				", service='" + service + '\'' +
				'}';
	}
}
