package bee.com.nascent.maven.plugin.process;

import bee.com.nascent.maven.plugin.BeeAsserts;
import bee.com.nascent.maven.plugin.process.junit.BeeJunitAsserts;
import java.util.Map;
import org.springframework.util.StringUtils;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
public class DefaultBeeHttpRequestBodySupport<T> implements BeeHttpRequestBodySupport<T> {

    private String requestUri;
    /**
     * 方法 Get/Post
     */
    private String method;
    private Map requestParams;
    private T body;
    private Class<T> clazz;

    public DefaultBeeHttpRequestBodySupport(Class<T> clazz) {
        this.clazz = clazz;
    }

    public DefaultBeeHttpRequestBodySupport() {

    }

    @Override
    public String method() {
        return method;
    }

    public DefaultBeeHttpRequestBodySupport<T> fromString(String str) {
        if (!StringUtils.isEmpty(str)) {
            body = __BeeEnvironment.getJsonParser().readValue(str, clazz);
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

    public BeeHttpRequestBodySupport fromBody(T body) {
        this.body = body;
        return this;
    }

    public BeeHttpRequestBodySupport requestParams(Map requestParams) {
        this.requestParams = requestParams;
        return this;
    }

    @Override
    public Map requestParams() {
        return requestParams;
    }

    @Override
    public String requestUrl() {
        return requestUri;
    }

    public DefaultBeeHttpRequestBodySupport onPost(String requestUri) {
        this.requestUri = requestUri;
        this.method = "POST";
        return this;
    }

    public DefaultBeeHttpRequestBodySupport onGet(String requestUri) {
        this.requestUri = requestUri;
        this.method = "GET";
        return this;
    }
}
