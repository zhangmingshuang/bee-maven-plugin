package bee.com.nascent.maven.plugin.process;

import bee.com.nascent.maven.plugin.BeeAsserts;
import bee.com.nascent.maven.plugin.process.__BeeEnvironment.Elapsed;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
public interface BeeHttpRequestBodySupport<T> {

    /**
     * 获取调用方法
     *
     * @return get / post
     */
    String method();

    /**
     * 进入断言
     *
     * @return
     */
    BeeAsserts<T> asserts();

    /**
     * 获取body
     *
     * @return
     */
    T body();

    /**
     * 输出Body
     *
     * @param prefix 前缀
     * @param os     输出流
     * @return
     */
    default BeeHttpRequestBodySupport<T> printlnBody(String prefix, OutputStream os) {
        T body = body();
        try {
            String bodyStr = prefix;
            if (body == null) {
                bodyStr = "null";
            } else if (body instanceof CharSequence) {
                bodyStr += body.toString();
            } else {
                bodyStr += __BeeEnvironment.getJsonParser().beanToString(body);
            }
            bodyStr += "\n";
            os.write(bodyStr.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IoRuntimeException(e);
        }
        return this;
    }

    /**
     * 输出Body
     *
     * @param os 输出流
     * @return
     */
    default BeeHttpRequestBodySupport<T> printlnBody(OutputStream os) {
        return this.printlnBody("body:", os);
    }

    /**
     * 输出Body
     *
     * @return
     */
    default BeeHttpRequestBodySupport<T> printlnBody() {
        return this.printlnBody("body:" + System.out);
    }

    /**
     * 输出Body
     *
     * @param prefix 前缀
     * @return
     */
    default BeeHttpRequestBodySupport<T> printlnBody(String prefix) {
        return this.printlnBody(prefix, System.out);
    }

    /**
     * 输出使用时间
     *
     * @return
     */
    default BeeHttpRequestBodySupport<T> printlnTimeUsed() {
        return this.printlnTimeUsed("timeUsed:", System.out);
    }

    /**
     * 输出使用时间
     *
     * @param prefix 前缀
     * @return
     */
    default BeeHttpRequestBodySupport<T> printlnTimeUsed(String prefix) {
        return this.printlnTimeUsed(prefix, System.out);
    }

    /**
     * 输出使用时间
     *
     * @param os 输出流
     * @return
     */
    default BeeHttpRequestBodySupport<T> printlnTimeUsed(OutputStream os) {
        return this.printlnTimeUsed("timeUsed:" + os);
    }

    /**
     * 输出使用时间
     *
     * @param prefix 前缀
     * @param os     输出流
     * @return
     */
    default BeeHttpRequestBodySupport<T> printlnTimeUsed(String prefix, OutputStream os) {
        this.timeUsed(message -> {
            try {
                String info = this.requestUrl() + "\n";
                info += (prefix + message);
                os.write(info.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new IoRuntimeException(e);
            }
        });
        return this;
    }

    /**
     * 获取使用时间
     *
     * @param consumer 消费器
     * @return
     */
    default BeeHttpRequestBodySupport<T> timeUsed(Consumer<String> consumer) {
        Elapsed elapsed = Elapsed.get();
        consumer.accept(elapsed.toString());
        return this;
    }

    /**
     * 获取使用时间
     *
     * @param key      使用时间中的Key {@link Elapsed}
     * @param consumer 消费器
     * @return
     */
    default BeeHttpRequestBodySupport<T> onTimeUsed(String key, Consumer<Long> consumer) {
        Elapsed elapsed = Elapsed.get();
        consumer.accept(elapsed.get(key));
        return this;
    }

    /**
     * 输出请求参数
     *
     * @param os 输出流
     * @return
     */
    default BeeHttpRequestBodySupport<T> printlnRequestParams(OutputStream os) {
        return this.printlnRequestParams("requestParams:", os);
    }

    /**
     * 输出请求参数
     *
     * @return
     */
    default BeeHttpRequestBodySupport<T> printlnRequestParams() {
        return this.printlnRequestParams("requestParams:", System.out);
    }

    /**
     * 输出请求参数
     *
     * @param prefix 前缀
     * @return
     */
    default BeeHttpRequestBodySupport<T> printlnRequestParams(String prefix) {
        return this.printlnRequestParams(prefix, System.out);
    }

    /**
     * 输出请求参数
     *
     * @param prefix 前缀
     * @param os     输出流
     * @return
     */
    default BeeHttpRequestBodySupport<T> printlnRequestParams(String prefix, OutputStream os) {
        Map params = this.requestParams();
        try {
            String paramsStr = prefix;
            if (params == null) {
                paramsStr += "null";
            } else {
                paramsStr += __BeeEnvironment.getJsonParser().beanToString(params);
            }
            paramsStr += "\n";
            os.write(paramsStr.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IoRuntimeException(e);
        }
        return this;
    }

    /**
     * 获取请求参数
     *
     * @param consumer 消费器
     * @return
     */
    default BeeHttpRequestBodySupport<T> requestParams(Consumer<Map> consumer) {
        consumer.accept(requestParams());
        return this;
    }

    /**
     * 获取请求参数
     *
     * @return
     */
    Map requestParams();

    /**
     * 获取请求地址
     *
     * @return
     */
    String requestUrl();
}
