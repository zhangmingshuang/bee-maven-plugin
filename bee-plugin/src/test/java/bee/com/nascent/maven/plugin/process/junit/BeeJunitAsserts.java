package bee.com.nascent.maven.plugin.process.junit;

import bee.com.nascent.maven.plugin.BeeAsserts;
import bee.com.nascent.maven.plugin.process.__BeeEnvironment;
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
    public BeeAsserts<T> isTrue(Function<T, Boolean> fun, String message) {
        Assert.assertTrue(message, fun.apply(body));
        return this;
    }

    @Override
    public BeeAsserts<T> isFalse(Function<T, Boolean> fun, String message) {
        Assert.assertFalse(message, fun.apply(body));
        return this;
    }

    @Override
    public BeeAsserts<T> isEquals(Function<T, Object> fun, Object eqValue, String message) {
        Assert.assertEquals(message, fun.apply(body), eqValue);
        return this;
    }

    @Override
    public BeeAsserts<T> isEquals(Function<T, Long> fun, long eqValue, String message) {
        Assert.assertEquals(message, fun.apply(body).longValue(), eqValue);
        return this;
    }

    @Override
    public BeeAsserts<T> isEquals(Function<T, Float> fun, float eqValue, String message) {
        Assert.assertEquals(message, fun.apply(body).floatValue(), eqValue);
        return this;
    }

    @Override
    public BeeAsserts<T> isEquals(Function<T, Double> fun, double eqValue, String message) {
        Assert.assertEquals(message, fun.apply(body).doubleValue(), eqValue);
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
        return __BeeEnvironment.getJsonParser()
            .writeValueAsString(this.body);
    }
}
