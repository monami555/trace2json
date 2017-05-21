package com.example.trace2json.process.impl;


import com.example.trace2json.Call;
import com.example.trace2json.pojo.Trace;
import com.example.trace2json.pojo.TraceRoot;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;


public class DefaultCallsProcessorTest
{
	private static final String TRACE1 = "trace1";
	private static final String TRACE2 = "trace2";
	private static final String SPAN1 = "aa";
	private static final String SPAN2 = "bb";
	private static final String SERVICE = "some-service";

	private DefaultCallsProcessor processor = new DefaultCallsProcessor();
	private LocalDateTime startTime;

	@Before
	public void setUp()
	{
		startTime = LocalDateTime.of(2000, 12, 1, 0, 1);
		processor.setEpsilon(Duration.ofMinutes(1));
		processor.setTraceNumberBuffer(0);
	}

	@Test
	public void testLastEndTimeUpdated()
	{
		Assert.assertEquals(null, processor.getLastEndTime());
		processor.processCall(new Call(startTime, startTime.plusMinutes(1), TRACE1, SERVICE, SPAN1, SPAN2));
		Assert.assertEquals(startTime.plusMinutes(1), processor.getLastEndTime());
		processor.processCall(new Call(startTime, startTime.plusMinutes(2), TRACE2, SERVICE, SPAN1, SPAN2));
		Assert.assertEquals(startTime.plusMinutes(2), processor.getLastEndTime());
	}

	@Test
	public void testLastEndTimeMissing()
	{
		processor.processCall(new Call(startTime, null, TRACE1, SERVICE, SPAN1, SPAN2));
		Assert.assertEquals(null, processor.getLastEndTime());
	}

	@Test
	public void testDoesNotPopUnfinishedTrace()
	{
		processor.processCall(new Call(startTime, startTime.plusMinutes(1), TRACE1, SERVICE, SPAN1, SPAN2));
		processor.processCall(new Call(startTime, startTime.plusMinutes(2), TRACE1, SERVICE, SPAN1, SPAN2));
		Assert.assertEquals(0, processor.popReadyTraces(false).size());
	}

	@Test
	public void testDoesNotPopFinishedTraceTooEarly()
	{
		processor.processCall(new Call(startTime, startTime.plusMinutes(1), TRACE1, SERVICE, SPAN1, SPAN2));
		processor.processCall(new Call(startTime, startTime.plusMinutes(2), TRACE1, SERVICE, SPAN1, SPAN2));
		processor.processCall(new Call(startTime, startTime.plusMinutes(3), TRACE1, SERVICE, null, SPAN2));
		Assert.assertEquals(0, processor.popReadyTraces(false).size());
	}

	@Test
	public void testPopsFinishedOldTrace()
	{
		processor.processCall(new Call(startTime, startTime.plusMinutes(1), TRACE1, SERVICE, SPAN2, "cc"));
		processor.processCall(new Call(startTime, startTime.plusMinutes(2), TRACE1, SERVICE, SPAN2, "dd"));
		processor.processCall(new Call(startTime, startTime.plusMinutes(3), TRACE1, SERVICE, null, SPAN2));
		processor.processCall(new Call(startTime, startTime.plusMinutes(5), TRACE2, SERVICE, SPAN1, SPAN2));

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

	@Test
	public void tesCircularLogsAreHandled()
	{
		processor.processCall(new Call(startTime, startTime.plusMinutes(1), TRACE1, SERVICE, SPAN2, SPAN1));
		processor.processCall(new Call(startTime, startTime.plusMinutes(2), TRACE1, SERVICE, SPAN1, SPAN2));
		processor.processCall(new Call(startTime, startTime.plusMinutes(3), TRACE1, SERVICE, null, SPAN2));
		processor.processCall(new Call(startTime, startTime.plusMinutes(5), TRACE2, SERVICE, SPAN1, SPAN2));
		final Collection<TraceRoot> traces = processor.popReadyTraces(false);
		Assert.assertEquals(1, traces.size());
		Assert.assertEquals(0, processor.popReadyTraces(false).size());
		Assert.assertEquals(TRACE1, traces.iterator().next().getId());
	}
}
