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

  /**
   * 请求时异常处理器
   *
   * @param e 异常消费器
   * @return
   */
  BeeHttpRequestSupport<T> onException(Consumer<Throwable> e);

  /**
   * 添加请求时Header
   *
   * @param headers 请求头
   * @return
   */
  default BeeHttpRequestSupport<T> header(Map<String, String> headers) {
    headers.forEach(
        (k, v) -> {
          this.header(k, v);
        });
    return this;
  }

  /**
   * 添加请求时头
   *
   * @param key 请求头
   * @param value 请求头对应值
   * @return
   */
  BeeHttpRequestSupport<T> header(String key, String value);

  /**
   * 执行post请求
   *
   * @return
   */
  default BeeHttpRequestBodySupport<T> doPost() {
    return this.doPost(
        __BeeEnvironment.Http.connectionTimeOut(), __BeeEnvironment.Http.readTimeOut());
  }

  /**
   * 执行post请求
   *
   * @param connectionTimeOut 连接超时时间
   * @param readTimeOut 请求超时时间
   * @return
   */
  BeeHttpRequestBodySupport<T> doPost(int connectionTimeOut, int readTimeOut);

  /**
   * 执行Get请求
   *
   * @return
   */
  default BeeHttpRequestBodySupport<T> doGet() {
    return this.doGet(
        __BeeEnvironment.Http.connectionTimeOut(), __BeeEnvironment.Http.readTimeOut());
  }

  /**
   * 执行Get请求
   *
   * @param connectionTimeOut 连接超时时间
   * @param readTimeOut 读取超时时间
   * @return
   */
  BeeHttpRequestBodySupport<T> doGet(int connectionTimeOut, int readTimeOut);

  /**
   * 模拟请求数据，如果有模拟请求数据，会直接响应模拟的数据
   *
   * @param mockData 模拟数据
   * @return
   */
  BeeHttpRequestBodySupport<T> mock(T mockData);
}
