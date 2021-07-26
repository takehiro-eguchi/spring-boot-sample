package com.egu.springboot.sample.service;

import java.util.Map;

import com.egu.springboot.sample.util.CollectionUtil.Node;

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
	private static final String NAME_SPLIT_STRING = ".";

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
		return json;
	}
}
