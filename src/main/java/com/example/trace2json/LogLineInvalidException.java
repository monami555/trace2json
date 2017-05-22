package com.example.trace2json;

/**
 * The exception thrown when log line is invalid.
 */
public class LogLineInvalidException extends RuntimeException
{
	public LogLineInvalidException(final String message)
	{
		super(message);
	}
}
