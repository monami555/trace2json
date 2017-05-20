package com.example.trace2json.pojo;

import java.util.Collection;


public class Trace
{
	private String service;
	private String start;
	private String end;
	private Collection<Trace> calls;

	public String getService()
	{
		return service;
	}

	public String getStart()
	{
		return start;
	}

	public String getEnd()
	{
		return end;
	}

	public Collection<Trace> getCalls()
	{
		return calls;
	}

	public void setService(final String service)
	{
		this.service = service;
	}

	public void setStart(final String start)
	{
		this.start = start;
	}

	public void setEnd(final String end)
	{
		this.end = end;
	}

	public void setCalls(final Collection<Trace> calls)
	{
		this.calls = calls;
	}
}
