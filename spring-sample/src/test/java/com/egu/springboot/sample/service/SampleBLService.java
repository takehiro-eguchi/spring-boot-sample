package com.egu.springboot.sample.service;

import org.springframework.stereotype.Service;

import lombok.Builder;
import lombok.Data;

/**
 * サンプル用のサービスです。
 * @author t-eguchi
 */
@Service
public class SampleBLService {

	/** サンプル用のリクエストです */
	@Data
	@Builder
	public static class SampleRequest {
		private String id;
		private String name;
	}

	/** サンプル用のレスポンスです */
	@Data
	@Builder
	public static class SampleResponse {
		private String id;
		private String name;
		private String memo;
	}

	/** テストメソッド1 */
	@BusinessLogic("Get")
	public SampleResponse get(SampleRequest request) {
		return SampleResponse.builder()
				.id(request.getId())
				.name(request.getName())
				.memo("GET")
				.build();
	}

	/** テストメソッド2 */
	@BusinessLogic(id = "Post")
	public SampleResponse post(SampleRequest request) {
		return SampleResponse.builder()
				.id(request.getId())
				.name(request.getName())
				.memo("POST")
				.build();
	}
}
