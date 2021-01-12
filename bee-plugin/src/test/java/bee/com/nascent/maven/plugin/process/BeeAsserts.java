package bee.com.nascent.maven.plugin.process;

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

  default bee.com.nascent.maven.plugin.BeeAsserts<T> notNull() {
    return this.notNull("assert that the body is not null, but unexpected.");
  }

  bee.com.nascent.maven.plugin.BeeAsserts<T> notNull(String message);

  default bee.com.nascent.maven.plugin.BeeAsserts<T> isNull() {
    return this.isNull("assert that the body is null, but unexpected.");
  }

  bee.com.nascent.maven.plugin.BeeAsserts<T> isNull(String message);

  default bee.com.nascent.maven.plugin.BeeAsserts<T> isTrue(Function<T, Boolean> fun) {
    return this.isTrue(
        fun, "assert that the value of function is true," + "but unexpected. body: " + bodyJson());
  }

  bee.com.nascent.maven.plugin.BeeAsserts<T> isTrue(Function<T, Boolean> fun, String message);

  default bee.com.nascent.maven.plugin.BeeAsserts<T> isFalse(Function<T, Boolean> fun) {
    return this.isFalse(
        fun, "assert that the value of function is false," + "but unexpected. body: " + bodyJson());
  }

  bee.com.nascent.maven.plugin.BeeAsserts<T> isFalse(Function<T, Boolean> fun, String message);

  default bee.com.nascent.maven.plugin.BeeAsserts<T> isEquals(
      Function<T, Object> fun, Object eqValue) {
    return this.isEquals(fun, eqValue, "expect is " + fun.apply(body()) + " but " + eqValue);
  }

  bee.com.nascent.maven.plugin.BeeAsserts<T> isEquals(
      Function<T, Object> fun, Object eqValue, String message);

  default bee.com.nascent.maven.plugin.BeeAsserts<T> isEquals(Function<T, Long> fun, long eqValue) {
    return this.isEquals(fun, eqValue, "expect is " + fun.apply(body()) + " but " + eqValue);
  }

  bee.com.nascent.maven.plugin.BeeAsserts<T> isEquals(
      Function<T, Long> fun, long eqValue, String message);

  default bee.com.nascent.maven.plugin.BeeAsserts<T> isEquals(
      Function<T, Float> fun, float eqValue) {
    return this.isEquals(fun, eqValue, "expect is " + fun.apply(body()) + " but " + eqValue);
  }

  bee.com.nascent.maven.plugin.BeeAsserts<T> isEquals(
      Function<T, Float> fun, float eqValue, String message);

  default bee.com.nascent.maven.plugin.BeeAsserts<T> isEquals(
      Function<T, Double> fun, double eqValue) {
    return this.isEquals(fun, eqValue, "expect is " + fun.apply(body()) + " but " + eqValue);
  }

  bee.com.nascent.maven.plugin.BeeAsserts<T> isEquals(
      Function<T, Double> fun, double eqValue, String message);

  T body();

  String bodyJson();

  default bee.com.nascent.maven.plugin.BeeAsserts<T> bodyJson(Consumer<String> consumer) {
    consumer.accept(bodyJson());
    return this;
  }
}
