package com.example.trace2json.process.impl;


import com.example.trace2json.Call;
import com.example.trace2json.pojo.Trace;
import com.example.trace2json.pojo.TraceRoot;
import com.example.trace2json.process.TraceBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;


public class DefaultTraceBuilder implements TraceBuilder
{
	private TraceRoot rootTrace;
	private Collection<Trace> traces;
	private LocalDateTime endTime;

	/**
	 * Creates a new instance of the processor for given trace ID.
	 *
	 * @param traceId the trace ID for this trace.
	 */
	public DefaultTraceBuilder(final String traceId)
	{
		this.rootTrace = new TraceRoot();
		this.rootTrace.setTrace(traceId);
		this.traces = new ArrayList<>();
	}

	@Override
	public void processCall(final Call call)
	{
		final Trace trace = new Trace();
		trace.setEnd(call.getEndTime());
		trace.setService(call.getService());
		trace.setStart(call.getStartTime());
		trace.setSpanId(call.getSpanId());
		trace.setCallerSpanId(call.getCallerSpanId());
		trace.setIsOrphaned(true);
		trace.setCalls(new ArrayList<>());

		if (call.getCallerSpanId() == null)
		{
			this.endTime = call.getEndTime();
			this.rootTrace.setRoot(trace);
			trace.setIsOrphaned(false);
		}
		traces.add(trace);
	}

	@Override
	public TraceRoot buildTrace()
	{
		for (Trace trace : traces)
		{
			Optional<Trace> predecessor = findPredecessor(traces, trace.getCallerSpanId());
			if (predecessor.isPresent())
			{
				predecessor.get().getCalls().add(trace);
				trace.setIsOrphaned(false);
			}
		}
		if (traces.stream().filter(t -> t.isOrphaned()).findAny().isPresent())
		{
			throw new IllegalArgumentException("The trace is not finished.");
		}
		return rootTrace;
	}

	private Optional<Trace> findPredecessor(final Collection<Trace> traces, final String spanId)
	{
		Optional<Trace> oot = traces
				.stream()
				.map(t -> findPredecessor(t.getCalls(), spanId))
				.filter(ot -> ot.isPresent())
				.findAny().orElse(null);

		Optional<Trace> maybeATrace = traces
				.stream()
				.filter(t -> t.getSpanId().equals(spanId))
				.findAny();

		if (!maybeATrace.isPresent())
		{
			maybeATrace = traces
					.stream()
					.map(t -> findPredecessor(t.getCalls(), spanId))
					.filter(ot -> ot.isPresent())
					.findAny()
					.orElse(Optional.empty());
		}

		return maybeATrace;
	}

	@Override
	public LocalDateTime getEndTimeOrNull()
	{
		return endTime;
	}

	@Override
	public String toString() {
		return rootTrace.getTrace();
	}
}
