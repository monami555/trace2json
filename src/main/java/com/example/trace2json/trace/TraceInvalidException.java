package com.example.trace2json.trace;

/**
 * The exception signaling that there has been not enough information received to complete the trace
 * tree or the information is corrupted.
 */
public class TraceInvalidException extends RuntimeException
{
	public TraceInvalidException(final String message)
	{
		super(message);
	}
}
