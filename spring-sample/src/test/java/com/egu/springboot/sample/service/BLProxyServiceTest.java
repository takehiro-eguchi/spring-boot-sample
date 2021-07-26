package com.egu.springboot.sample.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.egu.springboot.sample.service.SampleBLService.SampleRequest;
import com.egu.springboot.sample.service.SampleBLService.SampleResponse;

/**
 * {@link BLProxyService}を検証するためのクラスです。
 * @author t-eguchi
 */
@SpringBootTest
class BLProxyServiceTest {

	/** テスト対象 */
	@Autowired
	private BLProxyService target;

	@Test
	@DisplayName("サービスIDごとの実行を検証します。")
	void test() {
		SampleResponse actual = (SampleResponse)target.execute(
				"Get", SampleRequest.builder().id("id-1").name("name-1").build());
		assertEquals("id-1", actual.getId(), "id");
		assertEquals("name-1", actual.getName(), "name");
		assertEquals("GET", actual.getMemo(), "memo");

		actual = (SampleResponse)target.execute(
				"Post", SampleRequest.builder().id("id-2").name("name-2").build());
		assertEquals("id-2", actual.getId(), "id");
		assertEquals("name-2", actual.getName(), "name");
		assertEquals("POST", actual.getMemo(), "memo");

		assertThrows(
				RuntimeException.class,
				() -> target.execute("None", SampleRequest.builder().id("id-3").name("name-3").build()));
	}
}
