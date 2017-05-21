package com.example.trace2json.trace;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Custom {@link JsonSerializer} that defines how to serialise {@link LocalDateTime} fields.
 */
public class OutputDateTimeSerialiser extends JsonSerializer<LocalDateTime>
{
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	@Override
	public void serialize(LocalDateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException, JsonProcessingException
	{
		jsonGenerator.writeString(dateTime.format(dtf));
	}
}
