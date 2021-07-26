package com.egu.springboot.sample.service;

import java.util.Map;

/**
 * JSONのプロパティ名をリマップするコンポーネントです。
 * @author t-eguchi
 */
public class JsonPropertyNameMapper {

	/**
	 * プロパティ名の変更前と変更後のマッピングによりインスタンスを生成します。
	 * @param nameMapping
	 */
	public JsonPropertyNameMapper(Map<String, String> nameMapping) {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/**
	 * JSONのプロパティ名をリネームします。
	 * @param json
	 * @return
	 */
	public String renameProperties(String json) {
		return json;
	}
}
