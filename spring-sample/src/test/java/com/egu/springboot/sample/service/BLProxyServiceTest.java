package com.egu.springboot.sample.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BLProxyServiceTest {
	
	public static class SampleService {
		
	}

	@Autowired
	private BLProxyService target;

	@Test
	void test() {
		Object response = target.execute("Test1", null);
	}
}
