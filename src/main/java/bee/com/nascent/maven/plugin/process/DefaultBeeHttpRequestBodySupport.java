package bee.com.nascent.maven.plugin.process;

import org.springframework.util.StringUtils;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
public class DefaultBeeHttpRequestBodySupport<T> implements BeeHttpRequestBodySupport<T> {

    private T body;
    /**
     * 请求用时
     */
    private long ms;
    private Class<T> clazz;

    public DefaultBeeHttpRequestBodySupport(Class<T> clazz) {
        this.clazz = clazz;
    }

    public DefaultBeeHttpRequestBodySupport() {

    }

    public DefaultBeeHttpRequestBodySupport<T> fromString(String str) {
        if (!StringUtils.isEmpty(str)) {
            body = BeeEnvironment.getJsonParser().readValue(str, clazz);
        }
        return this;
    }

    @Override
    public BeeAsserts<T> asserts() {
        return new BeeJunitAsserts(body);
    }

    @Override
    public T body() {
        return this.body;
    }

    public DefaultBeeHttpRequestBodySupport timeUsed(long ms) {
        this.ms = ms;
        return this;
    }

    @Override
    public long timeUsed() {
        return ms;
    }

    public BeeHttpRequestBodySupport fromBody(T body) {
        this.body = body;
        return this;
    }
}
