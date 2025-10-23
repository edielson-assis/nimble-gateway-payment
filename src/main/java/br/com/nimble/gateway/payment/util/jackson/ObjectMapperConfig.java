package br.com.nimble.gateway.payment.util.jackson;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.SerializationFeature;

import br.com.nimble.gateway.payment.util.jackson.deser.LocalDateDeserializer;
import br.com.nimble.gateway.payment.util.jackson.deser.LocalDateTimeDeserializer;
import br.com.nimble.gateway.payment.util.jackson.ser.LocalDateSerializer;
import br.com.nimble.gateway.payment.util.jackson.ser.LocalDateTimeSerializer;


@Configuration
public class ObjectMapperConfig implements Jackson2ObjectMapperBuilderCustomizer {
	
	@Override
	public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
		jacksonObjectMapperBuilder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.serializerByType(LocalDate.class, new LocalDateSerializer())
				.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer())
				.deserializerByType(LocalDate.class, new LocalDateDeserializer())
				.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer());
	}
}