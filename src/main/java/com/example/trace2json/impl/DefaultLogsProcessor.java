package com.example.trace2json.impl;

import com.example.trace2json.LogLine;
import com.example.trace2json.LogLineInvalidException;
import com.example.trace2json.LogLineProcessor;
import com.example.trace2json.LogsProcessor;
import com.example.trace2json.trace.TraceInvalidException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
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
			final LocalDateTime startTime = LocalDateTime.now();
			int linesProcessed = 0;
			int errors = 0;

			String line;
			while ((line = reader.readLine()) != null)
			{
				try
				{
					final LogLine logLine = readLogLine(line);
					callProcessor.processLogLine(logLine);
					writer.writeAll(callProcessor.popReadyTraces(false));
					linesProcessed++;
				}
				catch (final LogLineInvalidException | TraceInvalidException e)
				{
					System.err.println(e.getMessage() + ", at line " + line);
					errors++;
				}
			}

			try
			{
				writer.writeAll(callProcessor.popReadyTraces(true));
			}
			catch (final TraceInvalidException e)
			{
				System.err.println(e.getMessage());
			}

			System.err.println(
					"Processing took " + Duration.between(startTime, LocalDateTime.now()).toMillis() + " ms, " +
							"processed " + linesProcessed + " lines, encountered " + errors + " errors.");
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
}
