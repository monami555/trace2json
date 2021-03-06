package com.example.trace2json.impl;


import com.example.trace2json.LogLine;
import com.example.trace2json.trace.Trace;
import com.example.trace2json.trace.TraceInvalidException;
import com.example.trace2json.trace.TraceRoot;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;


// A factory pattern could have been used in {@link DefaultLogLineProcessor}, or Spring Framework in order to
// inject mock TraceBuilders, so that we would have a truly unit test here.
// Currently it is partially an integration test.

public class DefaultLogLineProcessorTest
{
	private static final String TRACE1 = "trace1";
	private static final String TRACE2 = "trace2";
	private static final String SPAN1 = "aa";
	private static final String SPAN2 = "bb";
	private static final String SERVICE = "some-service";

	private DefaultLogLineProcessor processor;
	private LocalDateTime startTime;

	@Before
	public void setUp()
	{
		startTime = LocalDateTime.of(2000, 12, 1, 0, 1);
		processor = new DefaultLogLineProcessor();
		processor.setEpsilon(Duration.ofMinutes(1));
		processor.setTraceNumberBuffer(0);
	}

	@Test
	public void testLastEndTimeUpdated()
	{
		Assert.assertEquals(null, processor.getLastEndTime());
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(1), TRACE1, SERVICE, SPAN1, SPAN2));
		Assert.assertEquals(startTime.plusMinutes(1), processor.getLastEndTime());
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(2), TRACE2, SERVICE, SPAN1, SPAN2));
		Assert.assertEquals(startTime.plusMinutes(2), processor.getLastEndTime());
	}

	@Test
	public void testLastEndTimeMissing()
	{
		processor.processLogLine(new LogLine(startTime, null, TRACE1, SERVICE, SPAN1, SPAN2));
		Assert.assertEquals(null, processor.getLastEndTime());
	}

	@Test
	public void testDoesNotPopUnfinishedTrace()
	{
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(1), TRACE1, SERVICE, SPAN2, "cc"));
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(2), TRACE1, SERVICE, SPAN2, "dd"));
		Assert.assertEquals(0, processor.popReadyTraces(false).size());
	}

	@Test
	public void testDoesNotPopFinishedTraceTooEarly()
	{
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(1), TRACE1, SERVICE, SPAN2, "cc"));
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(2), TRACE1, SERVICE, SPAN2, "dd"));
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(3), TRACE1, SERVICE, null, SPAN2));
		Assert.assertEquals(0, processor.popReadyTraces(false).size());
	}

	@Test(expected = TraceInvalidException.class)
	public void testDoesPopUnfinishedTraceWhenForced()
	{
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(1), TRACE1, SERVICE, "ww", "cc"));
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(2), TRACE1, SERVICE, SPAN2, "dd"));
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(3), TRACE1, SERVICE, null, SPAN2));
		processor.popReadyTraces(true);
	}

	@Test
	public void testPopsFinishedOldTrace()
	{
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(1), TRACE1, SERVICE, SPAN2, "cc"));
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(2), TRACE1, SERVICE, SPAN2, "dd"));
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(3), TRACE1, SERVICE, null, SPAN2));
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(5), TRACE2, SERVICE, SPAN1, SPAN2));

		final Collection<TraceRoot> traces = processor.popReadyTraces(false);
		Assert.assertEquals(1, traces.size());
		Assert.assertEquals(0, processor.popReadyTraces(false).size());

		final TraceRoot traceRoot = traces.iterator().next();
		Assert.assertEquals(TRACE1, traceRoot.getId());

		final Trace trace = traceRoot.getRoot();
		Assert.assertEquals(null, trace.getCallerSpanId());
		Assert.assertEquals(SPAN2, trace.getSpan());
		Assert.assertEquals(2, trace.getCalls().size());
	}

	@Test(expected = TraceInvalidException.class)
	public void tesCircularLogsAreHandled()
	{
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(1), TRACE1, SERVICE, SPAN2, SPAN1));
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(2), TRACE1, SERVICE, SPAN1, SPAN2));
		processor.processLogLine(new LogLine(startTime, startTime.plusMinutes(3), TRACE1, SERVICE, null, SPAN2));
		processor.popReadyTraces(true);
	}
}
