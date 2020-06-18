package bee.com.nascent.maven.plugin.process;

import java.util.Map;
import java.util.function.Consumer;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
public interface BeeHttpRequestSupport<T> {

    BeeHttpRequestSupport<T> onException(Consumer<Throwable> e);

    default BeeHttpRequestSupport<T> header(Map<String, String> headers) {
        headers.forEach((k, v) -> {
            this.header(k, v);
        });
        return this;
    }

    BeeHttpRequestSupport<T> header(String key, String value);

    default BeeHttpRequestBodySupport<T> doPost() {
        return this.doPost(__BeeEnvironment.Http.connectionTimeOut(),
            __BeeEnvironment.Http.readTimeOut());
    }

    BeeHttpRequestBodySupport<T> doPost(int connectionTimeOut, int readTimeOut);

    default BeeHttpRequestBodySupport<T> doGet() {
        return this.doGet(__BeeEnvironment.Http.connectionTimeOut(),
            __BeeEnvironment.Http.readTimeOut());
    }

    BeeHttpRequestBodySupport<T> doGet(int connectionTimeOut, int readTimeOut);

    BeeHttpRequestBodySupport<T> mock(T mockData);

}
