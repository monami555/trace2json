package com.example.trace2json.impl;

import com.example.trace2json.LogLine;
import com.example.trace2json.LogLineInvalidException;
import com.example.trace2json.LogLineProcessor;
import com.example.trace2json.LogsProcessor;
import com.example.trace2json.Stats;
import com.example.trace2json.trace.TraceInvalidException;
import com.example.trace2json.trace.TraceRoot;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class DefaultLogsProcessor implements LogsProcessor
{
	private final Collection<DateTimeFormatter> dateTimeFormatters;
	private final LogLineProcessor callProcessor = new DefaultLogLineProcessor();
	private final ObjectMapper jsonMapper;

	public DefaultLogsProcessor()
	{
		this.dateTimeFormatters = Arrays
				.stream(ALLOWED_INPUT_DATE_TIME_FORMATS)
				.map(df -> DateTimeFormatter.ofPattern(df))
				.collect(Collectors.toList());
		this.jsonMapper = new ObjectMapper();
		this.jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	@Override
	public void process(final Reader inputReader, final Writer outputWriter)
	{
		try (BufferedReader reader = new BufferedReader(inputReader);
				SequenceWriter writer = jsonMapper
						.writer(new MinimalPrettyPrinter(System.getProperty("line.separator")))
						.writeValues(outputWriter))
		{

			// speed before introducing executor service
			// small 306ms, including 284 ms for building the trace trees
			// medium 4311 ms, including 3322 ms for building the trace trees

			// speed after introducing executor service
			// medium 4703 ms - worse

			final ExecutorService executorService = Executors.newWorkStealingPool(2);

			final Future<Stats> statsFuture = executorService.submit(new LogsReadingCallable(reader));

			executorService.submit(new TraceWritingRunnable(writer));

			try
			{
				final Stats stats = statsFuture.get();
				executorService.shutdown();

				// write the remaining
				writer.writeAll(callProcessor.popReadyTraces(true));

				System.err.println(
						"Processing took " + stats.getDuration().toMillis() + " ms, " +
								"processed " + stats.getLinesProcessed() + " lines, " +
								"encountered " + stats.getErrorLines() + " errors.");

				executorService.awaitTermination(1, TimeUnit.SECONDS);
			}
			catch (final TraceInvalidException e)
			{
				System.err.println(e.getMessage());
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			catch (ExecutionException e)
			{
				e.printStackTrace();
			}

		}
		catch (final IOException e)
		{
			System.err.println("Problem writing or reading file: " + e.getMessage());
		}
	}

	private LogLine readLogLine(final String line)
	{
		try
		{
			final String[] parts = line.split("\\s");
			final LocalDateTime start = parseDateTime(parts[0]);
			final LocalDateTime end = parseDateTime(parts[1]);
			final String traceId = parts[2];
			final String service = parts[3];
			final String[] spans = parts[4].split("->");
			final String callerSpanId = "null".equals(spans[0]) ? null : spans[0];
			final String spanId = spans[1];
			return new LogLine(start, end, traceId, service, callerSpanId, spanId);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			throw new LogLineInvalidException("Log line is missing information");
		}
	}

	private LocalDateTime parseDateTime(final String str)
	{
		for (DateTimeFormatter dtf : dateTimeFormatters)
		{
			try
			{
				return LocalDateTime.parse(str, dtf);
			}
			catch (DateTimeParseException e)
			{
				// keep trying; yes not a good practice in general, but here seems to be the simplest
			}
		}
		throw new LogLineInvalidException("Unknown date format: " + str);
	}

	private class LogsReadingCallable implements java.util.concurrent.Callable<Stats>
	{
		private final BufferedReader reader;

		LogsReadingCallable(final BufferedReader reader)
		{
			this.reader = reader;
		}

		@Override
		public Stats call() throws Exception
		{
			final Stats stats = new Stats();
			try
			{
				String line;
				while ((line = reader.readLine()) != null)
				{
					try
					{
						final LogLine logLine = readLogLine(line);
						callProcessor.processLogLine(logLine);

						stats.lineProcessed();
					}
					catch (final LogLineInvalidException | TraceInvalidException e)
					{
						System.err.println(e.getMessage() + ", at line " + line);
						stats.errorLine();
					}
				}
			}
			catch (final IOException ioe)
			{
				System.err.println("Problem writing or reading file: " + ioe.getMessage());
			}
			return stats;
		}
	}

	private class TraceWritingRunnable implements Runnable
	{
		private final SequenceWriter writer;

		public TraceWritingRunnable(final SequenceWriter writer)
		{
			this.writer = writer;
		}

		@Override
		public void run()
		{
			try
			{
				while (true)
				{
					Collection<TraceRoot> traces = callProcessor.popReadyTraces(false);
					if (traces.size() > 0)
					{
						writer.writeAll(traces);
					}
				}
			}
			catch (final IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}
}
