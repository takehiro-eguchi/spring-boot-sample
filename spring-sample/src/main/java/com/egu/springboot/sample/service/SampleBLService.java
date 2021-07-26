package com.egu.springboot.sample.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

import lombok.Builder;
import lombok.Data;

@Service
public class SampleBLService {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface BusinessLogic {
		@AliasFor("value")
		String id() default "";
		@AliasFor("id")
		String value() default "";
	}

	@Data
	@Builder
	public static class SampleRequest {
		private String id;
		private String name;
	}

	@Data
	@Builder
	public static class SampleResponse {
		private String id;
		private String name;
		private String memo;
	}

	@BusinessLogic("Get")
	public SampleResponse get(SampleRequest request) {
		return SampleResponse.builder()
				.id(request.getId())
				.name(request.getName())
				.memo("GET")
				.build();
	}

	@BusinessLogic(id = "Post")
	public SampleResponse post(SampleRequest request) {
		return SampleResponse.builder()
				.id(request.getId())
				.name(request.getName())
				.memo("POST")
				.build();
	}
}
