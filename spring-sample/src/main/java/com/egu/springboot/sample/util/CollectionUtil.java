package com.egu.springboot.sample.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * コレクションを扱うユーティリティクラスです。
 * @author t-eguchi
 *
 */
public class CollectionUtil {

	/**
	 * ノードを表すコレクションオブジェクトです。
	 * @author t-eguchi
	 * @param <T>
	 */
	public class Node<T> {

		/** 値 */
		@Getter
		@Setter
		private T value;

		/** 親ノード */
		@Getter
		@Setter
		private Node<T> parent;

		/** 子供のコレクション */
		private final List<Node<T>> children = new ArrayList<>();

		/**
		 * 子供のコレクションを取得します。
		 * @return
		 */
		public List<Node<T>> getChildren() {
			return this.children;
		}

		/**
		 * 子供を追加します。
		 * @param node
		 */
		public void addChild(Node<T> node) {
			children.add(node);
			node.setParent(this);
		}

		/**
		 * ルートノードかどうか
		 * @return
		 */
		public boolean isRoot() {
			return parent == null;
		}

		/**
		 * 自分までの経路に登場するノードを収集します。
		 * @return
		 */
		public List<Node<T>> getRouteNodes() {
			List<Node<T>> nodes = new ArrayList<>();

			// 親をたどりながら収集
			Node<T> current = this;
			do {
				nodes.add(current);
			} while ((current = current.getParent()) != null);

			// 反転
			Collections.reverse(nodes);
			return nodes;
		}

		/**
		 * 現在から加味して、指定する深さに存在するノードを取得します。
		 * @param depth
		 * @return
		 */
		public List<Node<T>> getDepthNodes(int depth) {
			List<Node<T>> nodes = new ArrayList<>();
			addDepthNode(nodes, this, depth);
			return nodes;
		}

		/** 対象のノードから指定する深さに該当するノードを収集します */
		private void addDepthNode(List<Node<T>> collectList, Node<T> target, int depth) {
			if (depth == 0) {
				collectList.add(target);
				return;
			}

			// 深さを1つ減らす
			depth--;
			for (var child : target.getChildren()) {
				addDepthNode(collectList, child, depth);
			}
		}
	}
}
