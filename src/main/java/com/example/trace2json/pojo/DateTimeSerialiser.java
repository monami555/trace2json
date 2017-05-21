package com.example.trace2json.pojo;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DateTimeSerialiser extends JsonSerializer<LocalDateTime>
{
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	@Override
	public void serialize(LocalDateTime arg0, JsonGenerator arg1, SerializerProvider arg2)
			throws IOException, JsonProcessingException
	{
		arg1.writeString(arg0.format(dtf));
	}
}
