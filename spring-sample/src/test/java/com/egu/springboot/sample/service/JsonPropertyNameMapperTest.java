package com.egu.springboot.sample.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link JsonPropertyNameMapper}の検証を行うクラスです。
 * @author t-eguchi
 */
class JsonPropertyNameMapperTest {

	@Test
	@DisplayName("認証エンドポイントの入力を想定した検証です。")
	void test1() {
		// 準備
		Map<String, String> names = new HashMap<>() {
			{
				put("CompanyCd", "COMPANY_CD");
				put("UserID", "USER_ID");
				put("Password", "PASSWORD");
			}
		};
		var target = new JsonPropertyNameMapper(names);

		// 検証
		String json = "{"
				+ "\"CompanyCd\":\"1000\","
				+ "\"UserID\":\"Admin\","
				+ "\"Password\":\"p@ssword\""
				+ "}";
		String actual = target.renameProperties(json);
		assertEquals(
				"{"
						+ "\"COMPANY_CD\":\"1000\","
						+ "\"USER_ID\":\"Admin\","
						+ "\"PASSWORD\":\"p@ssword\""
						+ "}",
				actual);
	}

	@Test
	@DisplayName("CSHMNTエンドポイントの入力を想定した検証です。")
	void test2() {
		// 準備
		Map<String, String> names = new HashMap<>() {
			{
				put("FundInfList", "FundInfs");
				put("FundInfList.PortCd", "FundInfList.Fund");
				put("FundInfList.EvalDt", "FundInfList.RtEvalDt");
			}
		};
		var target = new JsonPropertyNameMapper(names);

		// 検証
		String json = "{"
				+ "\"Name\":\"AAA\","
				+ "\"FundInfList\":["
				+ "{\"PortCd\":\"10000\",\"CurCd\":\"USD\",\"EvalDt\":\"20210726\"},"
				+ "{\"PortCd\":\"10001\",\"CurCd\":\"JPY\"},"
				+ "]"
				+ "}";
		String actual = target.renameProperties(json);
		assertEquals(
				"{"
						+ "\"Name\":\"AAA\","
						+ "\"FundInfs\":["
						+ "{\"Fund\":\"10000\",\"CurCd\":\"USD\",\"RtEvalDt\":\"20210726\"},"
						+ "{\"Fund\":\"10001\",\"CurCd\":\"JPY\"},"
						+ "]"
						+ "}",
				actual);
	}

	@Test
	@DisplayName("必要そうなパターンのリネームを検証します。")
	void test3() {
		// 準備
		Map<String, String> names = new HashMap<>() {
			{
				put("Name", "NameJp");
				put("NicknameList", "Nicknames");
				put("Pref", "Prefctr");
			}
		};
		var target = new JsonPropertyNameMapper(names);

		// 検証
		String json = "{"
				+ "\"Name\":\"t-eguchi\","
				+ "\"NicknameList\":[\"take\",\"egu\"]"
				+ "}";
		String actual = target.renameProperties(json);
		assertEquals(
				"{"
				+ "\"NameJP\":\"t-eguchi\","
				+ "\"Nicknames\":[\"take\",\"egu\"]"
				+ "}",
				actual);
	}
}
