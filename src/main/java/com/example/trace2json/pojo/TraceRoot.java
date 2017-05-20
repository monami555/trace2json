package com.example.trace2json.pojo;

public class TraceRoot
{
	private String trace;
	private Trace root;

	public String getTrace()
	{
		return trace;
	}

	public Trace getRoot()
	{
		return root;
	}

	public void setTrace(final String trace)
	{
		this.trace = trace;
	}

	public void setRoot(final Trace root)
	{
		this.root = root;
	}
}
