package bee.com.nascent.maven.plugin.process;

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

    default BeeHttpRequestBodySupport<T> doPost() {
        return this.doPost(3000, 3000);
    }

    BeeHttpRequestBodySupport<T> doPost(int connectionTimeOut, int readTimeOut);

    default BeeHttpRequestBodySupport<T> doGet() {
        return this.doGet(3000, 3000);
    }

    BeeHttpRequestBodySupport<T> doGet(int connectionTimeOut, int readTimeOut);

    BeeHttpRequestBodySupport<T> mock(T mockData);

}
