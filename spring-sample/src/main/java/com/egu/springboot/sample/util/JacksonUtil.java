package com.egu.springboot.sample.util;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

public final class JacksonUtil {

	private JacksonUtil() {}

	public static Object get(JSONObject jsonObject, String name) {
		try {
			return jsonObject.get(name);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public static void put(JSONObject jsonObject, String name, Object value) {
		try {
			jsonObject.put(name, value);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public static void remove(JSONObject jsonObject, String name) {
		jsonObject.remove(name);
	}

	public static JSONObject getJsonObject(JSONArray array, int index) {
		try {
			return array.getJSONObject(index);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
}
