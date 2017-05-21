package com.example.trace2json;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;


import com.example.trace2json.pojo.TraceRoot;
import com.example.trace2json.process.CallsProcessor;
import com.example.trace2json.process.impl.DefaultCallsProcessor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Scanner;


public class Trace2JsonProcessor
{
	public static final String LOG_PATTERN = "2013-10-23T10:12:35.345Z 2013-10-23T10:12:35.361Z eckakaau service5 22buxmqp->3wos67cv";

	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSz");
	private DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz");
	private DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");
	private ObjectMapper mapper;
	private Writer outputWriter;
	private Reader inputReader;
	private CallsProcessor logsProcessor = new DefaultCallsProcessor();
	private Scanner scanner;

	DateTimeFormatter strangeFormat = new DateTimeFormatterBuilder()
			.appendValue(MONTH_OF_YEAR, 2).appendLiteral("==").appendValue(YEAR, 4)
			.appendLiteral("--").appendValue(DAY_OF_MONTH, 2).toFormatter();


	public static void main(final String... args) throws IOException
	{
		final URL url = Resources.getResource("small-log.txt");
		final String input = Resources.toString(url, Charsets.UTF_8);
		Trace2JsonProcessor proc = new Trace2JsonProcessor(new FileReader(new File(url.getFile())), new PrintWriter(System.out));
		proc.process();
	}

	private void process() throws IOException
	{
		while (scanner.hasNext())
		{
			final LocalDateTime start = parseDateTime(scanner.next());
			final LocalDateTime end = parseDateTime(scanner.next());
			final String traceId = scanner.next();
			final String service = scanner.next();
			final String[] spans = scanner.next().split("->");
			String callerSpanId = spans[0];
			final String spanId = spans[1];
			if ("null".equals(callerSpanId))
			{
				callerSpanId = null;
			}
			logsProcessor.processCall(new Call(start, end, traceId, service, callerSpanId, spanId));
		}

		Collection<TraceRoot> traceRoots = logsProcessor.popReadyTraces(true);
		mapper.writer().writeValues(outputWriter).writeAll(traceRoots);

		scanner.close();
	}

	private LocalDateTime parseDateTime(final String str)
	{
		LocalDateTime dateTime;
		try
		{
			dateTime = LocalDateTime.parse(str, dtf);
		}
		catch (DateTimeParseException e)
		{
			try
			{
				dateTime = LocalDateTime.parse(str, dtf2);
			}
			catch (DateTimeParseException ee)
			{
				dateTime = LocalDateTime.parse(str, dtf3);
			}
		}
		return dateTime;
	}

	public Trace2JsonProcessor(final Reader inputReader, final Writer outputWriter)
	{
		this.outputWriter = outputWriter;
		this.inputReader = inputReader;
		scanner = new Scanner(inputReader);
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
}
