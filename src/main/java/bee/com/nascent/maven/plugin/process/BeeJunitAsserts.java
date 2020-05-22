package bee.com.nascent.maven.plugin.process;

import java.util.function.Function;
import org.junit.Assert;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
@SuppressWarnings("java:S4276")
public class BeeJunitAsserts<T> implements BeeAsserts<T> {

    private T body;

    public BeeJunitAsserts(T body) {
        this.body = body;
    }

    @Override
    public BeeAsserts<T> notNull() {
        Assert.assertNotNull(body);
        return this;
    }

    @Override
    public BeeAsserts<T> notNull(String message) {
        Assert.assertNotNull(message, body);
        return this;
    }

    @Override
    public BeeAsserts<T> isNull(String message) {
        Assert.assertNull(message, body);
        return this;
    }

    @Override
    public BeeAsserts<T> isNull() {
        Assert.assertNull(body);
        return this;
    }

    @Override
    public BeeAsserts<T> isTrue(Function<T, Boolean> fun) {
        return this.isTrue(fun, "body: " + bodyJson());
    }

    @Override
    public BeeAsserts<T> isTrue(Function<T, Boolean> fun, String message) {
        Assert.assertTrue(message, fun.apply(body));
        return this;
    }

    @Override
    public BeeAsserts<T> isFalse(Function<T, Boolean> fun) {
        return this.isFalse(fun, "body: " + bodyJson());
    }

    @Override
    public BeeAsserts<T> isFalse(Function<T, Boolean> fun, String message) {
        Assert.assertFalse(message, fun.apply(body));
        return this;
    }

    @Override
    public T body() {
        return this.body;
    }

    @Override
    public String bodyJson() {
        if (this.body == null) {
            return null;
        }
        return BeeEnvironment.getJsonParser()
            .writeValueAsString(this.body);
    }
}
