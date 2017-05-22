package com.example.trace2json;


import java.time.Duration;
import java.time.LocalDateTime;


/**
 * Simple class for tracking the processing progress.
 */
public class Stats
{
	private LocalDateTime startTime;
	private int linesProcessed;
	private int errorLines;
	private int errorTraces;
	private int tracesProcessed;

	public Stats()
	{
		this.startTime = LocalDateTime.now();
	}

	public void lineProcessed()

	{
		this.linesProcessed++;
	}

	public void errorLine()
	{
		this.errorLines++;
	}

	public void errorTrace()
	{
		this.errorTraces++;
	}

	public Duration getDuration()
	{
		return Duration.between(startTime, LocalDateTime.now());
	}

	public int getLinesProcessed()
	{
		return linesProcessed;
	}

	public int getErrorLines()
	{
		return errorLines;
	}

	public int getErrorTraces()
	{
		return errorTraces;
	}

	public void tracesProcessed(final int amount)
	{
		this.tracesProcessed = this.tracesProcessed + amount;
	}

	public int getTracesProcessed()
	{
		return tracesProcessed;
	}

}
