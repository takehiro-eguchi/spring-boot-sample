/**
 *
 */
package com.egu.springboot.sample.service;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.util.StringUtils;

/**
 * ビジネスロジックを表すための注釈です。
 * @author t-eguchi
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface BusinessLogic {

	/**
	 * 値を表す値です。
	 * @return
	 */
	@AliasFor("value")
	String id() default "";

	/**
	 * ビジネスロジックIDを表す値です。
	 * @return
	 */
	@AliasFor("id")
	String value() default "";

	/**
	 * ビジネスロジックアノテーションに利用するユーティリティです。
	 * @author t-eguchi
	 */
	static final class BusinessLogicUtil {

		/** デフォルトコンストラクタを隠蔽 */
		private BusinessLogicUtil() {}

		/**
		 * ビジネスロジックからIDを取得します。
		 * @param businessLogic
		 * @return
		 */
		public static String getId(BusinessLogic businessLogic) {
			String id = businessLogic.id();
			if (StringUtils.hasLength(id))
				return id;
			return businessLogic.value();
		}
	}
}
