package com.example.trace2json;

import com.example.trace2json.pojo.Trace;
import com.example.trace2json.pojo.TraceRoot;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * A test that checks whether the POJO structure gets correctly serialised by Jackson.
 */
public class SerialisationIntegrationTest
{
	private static ObjectMapper mapper;
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	@BeforeClass
	public static void setUpClass()
	{
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	@Test
	public void testSerialisationModel() throws IOException
	{
		final URL url = Resources.getResource("serialisation/expected-output.json");
		final String expectedOutput = Resources.toString(url, Charsets.UTF_8);

		final TraceRoot traceRoot = buildTraceRoot();
		final String serialisedOutput = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(traceRoot);

		Assert.assertEquals(
				expectedOutput.replaceAll("\\s", ""),
				serialisedOutput.replaceAll("\\s", ""));
	}

	private TraceRoot buildTraceRoot()
	{
		final TraceRoot traceRoot = new TraceRoot();
		traceRoot.setTrace("trace1");
		final Trace frontendTrace = new Trace();
		traceRoot.setRoot(frontendTrace);

		frontendTrace.setService("front-end");
		frontendTrace.setStart(LocalDateTime.parse("2016-10-20 12:43:32.000", dtf));
		frontendTrace.setEnd(LocalDateTime.parse("2016-10-20 12:43:42.000", dtf));
		final List<Trace> frontendCalls = new ArrayList<>();
		frontendTrace.setCalls(frontendCalls);

		final Trace backend1Trace = new Trace();
		backend1Trace.setService("back-end-1");
		backend1Trace.setStart(LocalDateTime.parse("2016-10-20 12:43:33.000", dtf));
		backend1Trace.setEnd(LocalDateTime.parse("2016-10-20 12:43:36.000", dtf));
		final List<Trace> backend1Calls = new ArrayList<>();
		backend1Trace.setCalls(backend1Calls);
		frontendCalls.add(backend1Trace);

		final Trace backend3Trace = new Trace();
		backend3Trace.setService("back-end-3");
		backend3Trace.setStart(LocalDateTime.parse("2016-10-20 12:43:34.000", dtf));
		backend3Trace.setEnd(LocalDateTime.parse("2016-10-20 12:43:35.000", dtf));
		backend1Calls.add(backend3Trace);

		final Trace backend2Trace = new Trace();
		backend2Trace.setService("back-end-2");
		backend2Trace.setStart(LocalDateTime.parse("2016-10-20 12:43:38.000", dtf));
		backend2Trace.setEnd(LocalDateTime.parse("2016-10-20 12:43:40.000", dtf));
		frontendCalls.add(backend2Trace);

		return traceRoot;
	}
}
