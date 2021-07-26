package com.egu.springboot.sample.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.egu.springboot.sample.util.CollectionUtil.Node;
import com.egu.springboot.sample.util.JacksonUtil;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * JSONのプロパティ名をリマップするコンポーネントです。
 * @author t-eguchi
 */
public class JsonPropertyNameMapper {

	/** マッピングされている名前 */
	@Data
	@AllArgsConstructor
	private static class MappedName {
		private String from;
		private String to;
	}

	/** 名前の区切り文字 */
	private static final String NAME_SPLIT_STRING = "\\.";

	/** ルートノード */
	private Node<MappedName> rootNode = new Node<>();

	/**
	 * プロパティ名の変更前と変更後のマッピングによりインスタンスを生成します。
	 * @param nameMapping
	 */
	public JsonPropertyNameMapper(Map<String, String> nameMapping) {
		// 変更前と変更後のマッピングノードを作成する
		nameMapping.forEach((from, to) -> {
			// .区切りで分割する
			String[] fromNames = from.split(NAME_SPLIT_STRING);
			String[] toNames = to.split(NAME_SPLIT_STRING);

			// サイズが異なる場合はエラーとする
			if (fromNames.length != toNames.length)
				throw new IllegalArgumentException(
						"Rename property names must be same depth."
								+ "from = " + from + ", to = " + to);

			// 末尾のみfromとtoを設定し、それ以外はfromのみ設定し、ノードを作成する
			Node<MappedName> currentNode = rootNode;
			int lastIndex = fromNames.length - 1;
			for (int index = 0; index <= lastIndex; index++) {
				// ノードを取得
				String fromName = fromNames[index];
				currentNode = getFromNode(currentNode, fromName);

				// 末尾の場合はtoを設定
				if (index == lastIndex) {
					var mappedName = currentNode.getValue();
					mappedName.setTo(to);
				}
			}
		});
	}

	/** 合致するノードを検索して返し、存在しなければ作成して返します。 */
	private Node<MappedName> getFromNode(
			Node<MappedName> targetNode, String from) {
		var result = targetNode.getChildren().stream()
				.filter(node -> node.getValue().getFrom().equals(from))
				.findFirst();

		// 存在していればそのまま返す
		if (result.isPresent())
			return result.get();

		// 存在していなければ作成及び追加して返す
		MappedName name = new MappedName(from, null);
		Node<MappedName> node = new Node<>(name);
		targetNode.addChild(node);

		return node;
	}

	/**
	 * JSONのプロパティ名をリネームします。
	 * @param json
	 * @return
	 */
	public String renameProperties(String json) {
		try {
			// JsonObjectに変換
			JSONObject jsonObject = new JSONObject(json);

			// 最大の深さを算出
			int maxDepth = rootNode.getMaxDepth();

			// 深いところからプロパティ名の変更を行っていく
			for (int depth = maxDepth; 0 < depth; depth--) {
				// 該当する深さのノードを取得
				List<Node<MappedName>> nodes = rootNode.getDepthNodes(depth);

				// リネームの実行
				renameProperties(jsonObject, nodes);
			}

			// jsonへ戻す
			return jsonObject.toString();

		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	/** JSONのリネームを行います */
	private void renameProperties(JSONObject jsonObject, List<Node<MappedName>> targetNodes) {
		for (var targetNode : targetNodes) {
			var routeNodes = targetNode.getRouteNodes();
			Object current = jsonObject;
			int lastIndex = routeNodes.size() - 1;
			for (int i = 0; i <= lastIndex; i++) {
				var node = routeNodes.get(i);

				// ルートはスキップ
				if (node.isRoot())
					continue;

				// 最後の要素であれば、リネームする
				if (i == lastIndex) {
					renameProperty(current, node.getValue());
					break;
				}

				// ターゲットを取得
				current = getCurrent(current, node.getValue());
			}
		}
	}

	/** プロパティのリネームを実行します */
	@SuppressWarnings("unchecked")
	private void renameProperty(Object current, MappedName name) {
		if (current == null)
			return;

		// JSONオブジェクトの場合はそのままリネーム
		if (current instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) current;
			renameProperty(jsonObject, name.getFrom(), name.getTo());
			return;
		}

		// JSONオブジェクトのコレクションの場合
		if (current instanceof List<?>) {
			for (JSONObject jsonObject : (List<JSONObject>) current) {
				renameProperty(jsonObject, name.getFrom(), name.getTo());
			}
			return;
		}

		throw new IllegalArgumentException(current.getClass() + " must not be set.");
	}

	/** JSONオブジェクトのプロパティ名を変更します */
	private void renameProperty(JSONObject jsonObject, String from, String to) {
		if (jsonObject == null)
			return;

		try {
			Object targetObject = jsonObject.get(from);
			jsonObject.remove(from);
			jsonObject.put(to, targetObject);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	/** ターゲットを取得します */
	private Object getCurrent(Object current, MappedName name) {
		if (current == null)
			return null;

		String fromName = name.getFrom();

		// JSONオブジェクトの場合
		if (current instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) current;
			Object obj = JacksonUtil.get(jsonObject, fromName);
			if (obj instanceof JSONObject)
				return obj;
			if (obj instanceof JSONArray)
				return getObjectList((JSONArray) obj);

			throw new IllegalArgumentException(obj + " must not be assigned.");
		}

		// JSON配列の場合
		if (current instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray) current;
			return getObjectList(jsonArray, fromName);
		}

		throw new IllegalArgumentException(current + " must not be assigned.");
	}

	/** JSON配列をオブジェクトのコレクションに変換します */
	private Object getObjectList(JSONArray array) {
		List<JSONObject> objectList = new ArrayList<>();
		for (int index = 0; index < array.length(); index++) {
			JSONObject jsonObject = JacksonUtil.getJsonObject(array, index);
			objectList.add(jsonObject);
		}
		return objectList;	}

	/** JSON配列をオブジェクトのコレクションに変換します */
	private Object getObjectList(JSONArray array, String name) {
		List<Object> objectList = new ArrayList<>();
		for (int index = 0; index < array.length(); index++) {
			JSONObject jsonObject = JacksonUtil.getJsonObject(array, index);
			Object object = JacksonUtil.get(jsonObject, name);
			objectList.add(object);
		}
		return objectList;
	}
}
