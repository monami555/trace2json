package com.example.trace2json.trace;

/**
 * The exception signaling that there has been not enough information received to complete the trace.
 */
public class TraceIncompleteException extends RuntimeException
{
	public TraceIncompleteException(final String message)
	{
		super(message);
	}
}
