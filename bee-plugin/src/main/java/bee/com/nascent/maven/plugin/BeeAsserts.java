package bee.com.nascent.maven.plugin;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/16
 */
@SuppressWarnings("java:S4276")
public interface BeeAsserts<T> {

  /**
   * 非空
   *
   * @return this
   */
  default BeeAsserts<T> notNull() {
    return this.notNull("assert that the body is not null, but unexpected.");
  }

  /**
   * 非空
   *
   * @param message 提示消息
   * @return
   */
  BeeAsserts<T> notNull(String message);

  /**
   * 空
   *
   * @return
   */
  default BeeAsserts<T> isNull() {
    return this.isNull("assert that the body is null, but unexpected.");
  }

  /**
   * 空
   *
   * @param message 提示消息
   * @return
   */
  BeeAsserts<T> isNull(String message);

  /**
   * true
   *
   * @param fun 判断器
   * @return
   */
  default BeeAsserts<T> isTrue(Function<T, Boolean> fun) {
    return this.isTrue(
        fun, "assert that the value of function is true," + "but unexpected. body: " + this
            .bodyJson());
  }

  /**
   * true
   *
   * @param fun 判断器
   * @param message 提示消息
   * @return
   */
  BeeAsserts<T> isTrue(Function<T, Boolean> fun, String message);

  /**
   * false
   *
   * @param fun 判断器
   * @return
   */
  default BeeAsserts<T> isFalse(Function<T, Boolean> fun) {
    return this.isFalse(
        fun, "assert that the value of function is false," + "but unexpected. body: " + this
            .bodyJson());
  }

  /**
   * false
   *
   * @param fun 判断器
   * @param message 提示消息
   * @return
   */
  BeeAsserts<T> isFalse(Function<T, Boolean> fun, String message);

  /**
   * 相等
   *
   * @param fun 判断器
   * @param eqValue 判断相等值
   * @return
   */
  default BeeAsserts<T> isEquals(Function<T, Object> fun, Object eqValue) {
    return this.isEquals(fun, eqValue, "expect is " + fun.apply(this.body()) + " but " + eqValue);
  }

  /**
   * 相等
   *
   * @param fun 判断器
   * @param eqValue 判断相等值
   * @param message 提示消息
   * @return
   */
  BeeAsserts<T> isEquals(Function<T, Object> fun, Object eqValue, String message);

  /**
   * 相等
   *
   * @param fun 判断器
   * @param eqValue 判断相等值
   * @return
   */
  default BeeAsserts<T> isEquals(Function<T, Long> fun, long eqValue) {
    return this.isEquals(fun, eqValue, "expect is " + fun.apply(this.body()) + " but " + eqValue);
  }

  /**
   * 相等
   *
   * @param fun 判断器
   * @param eqValue 判断相等值
   * @param message 提示消息
   * @return
   */
  BeeAsserts<T> isEquals(Function<T, Long> fun, long eqValue, String message);

  /**
   * 相等
   *
   * @param fun 判断器
   * @param eqValue 判断相等值
   * @return
   */
  default BeeAsserts<T> isEquals(Function<T, Float> fun, float eqValue) {
    return this.isEquals(fun, eqValue, "expect is " + fun.apply(this.body()) + " but " + eqValue);
  }

  /**
   * 相等
   *
   * @param fun 判断器
   * @param eqValue 判断相等值
   * @param message 提示消息
   * @return
   */
  BeeAsserts<T> isEquals(Function<T, Float> fun, float eqValue, String message);

  /**
   * 相等
   *
   * @param fun 判断器
   * @param eqValue 判断相等值
   * @return
   */
  default BeeAsserts<T> isEquals(Function<T, Double> fun, double eqValue) {
    return this.isEquals(fun, eqValue, "expect is " + fun.apply(this.body()) + " but " + eqValue);
  }

  /**
   * 相等
   *
   * @param fun 判断器
   * @param eqValue 判断相等值
   * @param message 提示消息
   * @return
   */
  BeeAsserts<T> isEquals(Function<T, Double> fun, double eqValue, String message);

  /**
   * body
   *
   * @return
   */
  T body();

  /**
   * body转换成JSON
   *
   * @return
   */
  String bodyJson();

  /**
   * body转换成JSON
   *
   * @param consumer body json 消费器
   * @return
   */
  default BeeAsserts<T> bodyJson(Consumer<String> consumer) {
    consumer.accept(this.bodyJson());
    return this;
  }
}
