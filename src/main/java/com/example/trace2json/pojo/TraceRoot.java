package com.example.trace2json.pojo;

public class TraceRoot
{
	private String id;
	private Trace root;

	public String getId()
	{
		return id;
	}

	public Trace getRoot()
	{
		return root;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public void setRoot(final Trace root)
	{
		this.root = root;
	}
}
