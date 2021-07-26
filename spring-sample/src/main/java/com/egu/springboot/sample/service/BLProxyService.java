package com.egu.springboot.sample.service;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.egu.springboot.sample.service.BusinessLogic.BusinessLogicUtil;

import lombok.AllArgsConstructor;

/**
 * BLサービスのプロキシとしての役割を担当するサービスクラスです。
 * @author t-eguchi
 */
@Service
public class BLProxyService {

	/** 実行可能なセットです */
	@AllArgsConstructor
	private static class ExecutableSet {

		/** Bean */
		private final Object bean;

		/** 実行メソッド */
		private final Method method;

		/**
		 * オブジェクトを引数に実行します。
		 * @param arg
		 * @return
		 */
		public Object execute(Object arg) {
			return ReflectionUtils.invokeMethod(
					method, bean, arg);
		}
	}

	/** アプリケーションコンテキスト */
	private final ApplicationContext context;

	/** 実行可能Beanのキャッシュ */
	private final Map<String, ExecutableSet> executableSetCache = new ConcurrentHashMap<>();

	/**
	 * アプリケーションコンテキストを渡し、インスタンスを生成します。
	 * @param context
	 */
	public BLProxyService(ApplicationContext context) {
		this.context = context;
	}

	/**
	 * サービスIDと入力値を渡すことにより、処理を実行します。
	 * @param serviceID
	 * @param input
	 * @return
	 */
	public Object execute(String serviceID, Object input) {
		// サービスIDに該当する実行可能Beanを取得
		ExecutableSet executableSet = getExecutableSet(serviceID);

		// 実行結果を返す
		Object result = executableSet.execute(input);
		return result;
	}

	/** サービスIDから実行可能Beanを取得します */
	private ExecutableSet getExecutableSet(String serviceID) {
		// キャッシュにあればそれを利用
		ExecutableSet executableSet = executableSetCache.get(serviceID);
		if (executableSet != null)
			return executableSet;

		// Beanの一覧を取得
		List<Object> beans = getBeans();

		// Beanの中からサービスIDに合致するメソッドを検索
		for (Object bean : beans) {
			Method[] methods = bean.getClass().getMethods();
			for (Method method : methods) {
				BusinessLogic businessLogic = method.getAnnotation(
						BusinessLogic.class);
				if (businessLogic == null)
					continue;

				String businessLogicID = BusinessLogicUtil.getId(businessLogic);
				if (businessLogicID.equals(serviceID)) {
					executableSet = new ExecutableSet(bean, method);
					break;
				}
			}
		}

		// 検索出来なかった場合は例外
		if (executableSet == null) {
			throw new RuntimeException("Service[" + serviceID + "] is not found.");
		}

		// キャッシュに追加し、リターン
		executableSetCache.put(serviceID, executableSet);
		return executableSet;
	}

	/** 登録されているBeanの一覧を取得します */
	private List<Object> getBeans() {
		// Bean名一覧→Beanへ変換→収集
		String[] beanNames = context.getBeanDefinitionNames();
		List<Object> beans = Arrays.stream(beanNames)
				.map(beanName -> context.getBean(beanName))
				.collect(Collectors.toList());
		return beans;
	}
}
