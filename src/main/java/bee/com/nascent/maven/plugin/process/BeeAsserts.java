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

    BeeAsserts<T> notNull();

    BeeAsserts<T> notNull(String message);

    BeeAsserts<T> isNull(String message);

    BeeAsserts<T> isNull();

    BeeAsserts<T> isTrue(Function<T, Boolean> fun);

    BeeAsserts<T> isTrue(Function<T, Boolean> fun, String message);

    BeeAsserts<T> isFalse(Function<T, Boolean> fun);

    BeeAsserts<T> isFalse(Function<T, Boolean> fun, String message);

    T body();

    String bodyJson();

    default BeeAsserts<T> bodyJson(Consumer<String> consumer) {
        consumer.accept(bodyJson());
        return this;
    }
}
